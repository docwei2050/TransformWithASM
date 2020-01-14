package com.docwei.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.objectweb.asm.*

class MyTransform extends Transform {
    Set<String> superNames

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        //将我们要修改的类的父类的class名放入集合
        Set<String> superNames = new HashSet<>();
        superNames.add("com/docwei/transformwithasm/activity/BaseActivity");

        //是否支持增量编译 
        boolean isIncremental = transformInvocation.incremental;


        //获取输入相关
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //获取输出相关 默认是空
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();



        for (TransformInput input : inputs) {
            // 有两种不同的输入 1.JarInput    2.directoryInput

            //JarInput对应本地的jar和远端的jar
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR
                );
                //处理文件 将修改过后的字节码copy到dest，就可以实现编译器干预字节码的目的了
                //这里不会修改本地的jar和远端的jar只做复制操作即可
                FileUtils.copyFile(jarInput.getFile(), dest);
            }

            //directoryInput对应我们写的代码
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {

                //directoryInput.getFile().getAbsolutePath() == Users/xxx/Downloads/TransformDemo/app/build/intermediates/javac/debug/classes

                directoryInput.file.eachFileRecurse { File file ->

                    if (file.isFile() && shouldProcessClassName(file.name)) {
                        FileInputStream fis = new FileInputStream(file);
                        ClassReader cr = new ClassReader(fis);
                        //接下来要过滤出BaseActivity的子类，然后去处理
                        if (hasImplSpecifiedInterfaces(cr, superNames)) {
                            byte[] bytes = scanClass(cr)
                            FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + file.name)
                            fos.write(bytes)
                            fos.close()
                        }
                        fis.close()
                    }
                }

                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY)
                //文件夹也要处理一下
                FileUtils.copyDirectory(directoryInput.getFile(), dest)
            }
        }


    }

    @Override
    String getName() {
        return "docweiTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }
    /**
     * 判断扫描的类的包名是否是.class     排除系统的R文件和BuildConfig文件
     */
    static boolean shouldProcessClassName(String name) {
        return name.endsWith(".class") && !name.startsWith("R\$") &&
                "R.class" != name && "BuildConfig.class" != name
    }


    private static byte[] scanClass(ClassReader cr) {
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.SKIP_DEBUG)
        return cw.toByteArray()
    }

    static class ScanClassVisitor extends ClassVisitor {


        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }
        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name == 'onCreate' && desc == '(Landroid/os/Bundle;)V') {
                // 先获取原始的方法
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions)
                //再修改方法
                return new ScanMethodVisitor(Opcodes.ASM6, mv)
            }
            return super.visitMethod(access, name, desc, signature, exceptions)

        }
    }


    static class ScanMethodVisitor extends MethodVisitor {


        ScanMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv)
        }

        @Override
        void visitCode() {
            //此方法在访问方法的头部时被访问到，仅被访问一次
            //此处可插入新的指令
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/docwei/transformwithasm/AppStatusManager", "getInstance", "()Lcom/docwei/transformwithasm/AppStatusManager;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/docwei/transformwithasm/AppStatusManager", "getAppStatus", "()I", false);
            mv.visitInsn(Opcodes.ICONST_M1);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            //尽管这里是MainActivity 但字节码那里显示this
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/docwei/transformwithasm/activity/BaseActivity", "protectApp", "()V", false);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/docwei/transformwithasm/activity/BaseActivity", "onCreate", "(Landroid/os/Bundle;)V", false);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitLabel(l1);

        }

        @Override
        void visitInsn(int opcode) {
            //此方法可以获取方法中每一条指令的操作类型，被访问多次
            //如应在方法结尾处添加新指令，则应判断：
            super.visitInsn(opcode)
        }
    }

    /**
     *
     * 获取父类是BaseActivity的子类
     */
    static boolean hasImplSpecifiedInterfaces(ClassReader reader, Set<String> superClazzs) {
        if ("java/lang/Object".equals(reader.getClassName())) {
            return false;
        }
        try {
            if (superClazzs.contains(reader.getSuperName())) {
                return true;
            } else {
                ClassReader parent = new ClassReader(reader.getSuperName());
                return hasImplSpecifiedInterfaces(parent, superClazzs);
            }
        } catch (IOException e) {
            return false;
        }
    }


}