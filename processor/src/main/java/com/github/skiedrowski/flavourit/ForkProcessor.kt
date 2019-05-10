package com.github.skiedrowski.flavourit

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ForkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME, ForkProcessor.KAPT_FLAVOURIT_FLAVOUR_OPTION_NAME)
class ForkProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val KAPT_FLAVOURIT_FLAVOUR_OPTION_NAME = "flavourit.flavour"
    }

    private lateinit var generatedSourcesRoot: String
    private lateinit var activeFlavour: String

    private val forkClass = Fork::class.java
    private val forksClass = Forks::class.java

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        generatedSourcesRoot = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        activeFlavour = processingEnv.options[KAPT_FLAVOURIT_FLAVOUR_OPTION_NAME].orEmpty()
        checkEnvironment()

        roundEnv.getElementsAnnotatedWith(forkClass).forEach { element ->
            validate(element, forkClass)
            generateNewMethod(element as TypeElement, element.getAnnotationsByType(forkClass))
        }
        roundEnv.getElementsAnnotatedWith(forksClass).forEach { element ->
            validate(element, forksClass)
            generateNewMethod(element as TypeElement, element.getAnnotation(forksClass).value)
        }
        return false
    }

    private fun generateNewMethod(typeElement: TypeElement, forkAnnotations: Array<out Fork>) {
        val packageOfType = processingEnv.elementUtils.getPackageOf(typeElement).toString()
        val file = File(generatedSourcesRoot)
        file.mkdirs()
        val fileSpecBuilder = FileSpec.builder(packageOfType, "${typeElement.simpleName}Ext")

        forkAnnotations.forEach { forkAnnotation ->
            val fromMethodName = forkAnnotation.from
            val toMethodName = forkAnnotation.to
            val forkAnnFlavour = forkAnnotation.ifActive
            if (activeFlavour == forkAnnFlavour) {
                val funcBuilder = FunSpec.builder(fromMethodName)
                    .addModifiers(KModifier.PUBLIC)
                    .receiver(ClassName(packageOfType, typeElement.simpleName.toString()))

                funcBuilder.addStatement("%L()", toMethodName)
                funcBuilder.addStatement(
                    "println(\"from: %L - to: %L - ifActive: %L\")", fromMethodName, toMethodName, forkAnnFlavour
                )
                fileSpecBuilder.addFunction(funcBuilder.build())
            }
        }
        fileSpecBuilder.build().writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(forkClass.canonicalName, forksClass.canonicalName)
    }

    private fun checkEnvironment() {
        if (generatedSourcesRoot.isEmpty()) {
            val msg = "Can't find the target directory for generated Kotlin files."
            processingEnv.messager.errorMessage { msg }
            throw IllegalArgumentException(msg)
        }
        if (activeFlavour.isEmpty()) {
            val msg = "active flavour needs to be specified via kapt option '$KAPT_FLAVOURIT_FLAVOUR_OPTION_NAME'"
            processingEnv.messager.errorMessage { msg }
            throw IllegalArgumentException(msg)
        }
    }

    private fun validate(element: Element, clazz: Class<*>) {
        if (element.kind != ElementKind.CLASS) {
            val msg = "${clazz.simpleName} annotation can only be applied to classes,  element: $element "
            processingEnv.messager.errorMessage { msg }
            throw IllegalArgumentException(msg)
        }
    }

}