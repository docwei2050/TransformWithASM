package com.docwei.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    //这里的projec就是应用这个插件的project
    @Override
    void apply(Project project) {

       def  extension=project.extensions.create("DocweiPlugin",MyExtension)
        //通过反射创建AppExtension对象
        AppExtension appExtension=project.extensions.getByType(AppExtension);

        //注册
        appExtension.registerTransform(new MyTransform());

        //配置阶段
        project.afterEvaluate {
            //配置完成时已经读取了task
        }

    }
}