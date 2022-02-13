package io.github.petertrr.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

class MyComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(project, MyExtension())
    }
}

class MyExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.accept(MyIrVisitor(), null)
    }
}

class MyIrVisitor : IrElementVisitor<IrElement?, IrElement?> {
    override fun visitElement(element: IrElement, data: IrElement?): IrElement? = null

    override fun visitClass(declaration: IrClass, data: IrElement?): IrElement {
        val myAnnotation = declaration.annotations.find {
            it.type.classFqName == FqName("io.github.petertrr.plugin.BuildFromPartial")
        }
//        if (myAnnotation == null) return null
//        else
        return TODO()
    }
}
