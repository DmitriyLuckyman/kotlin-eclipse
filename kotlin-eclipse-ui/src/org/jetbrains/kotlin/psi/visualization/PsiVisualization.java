package org.jetbrains.kotlin.psi.visualization;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;

public class PsiVisualization extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        KotlinEditor editor = (KotlinEditor) HandlerUtil.getActiveEditor(event);
        
        IFile iFile = ((IFileEditorInput) editor.getEditorInput()).getFile();
        File file = new File(iFile.getRawLocation().toOSString());
        
        String sourceCode = editor.getDocumentProvider().getDocument(HandlerUtil.getActiveEditorInput(event)).get();
        
        new VisualizationPage(shell, sourceCode, file).open();
        
        return null;
    }

}
