package org.jetbrains.kotlin.ui.editors.codeassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.jetbrains.kotlin.ui.editors.KeywordManager;

public class CompletionProcessor implements IContentAssistProcessor, ICompletionListener {
     
    /**
     * Characters for auto activation proposal computation.
     */
    private static final char[] VALID_PROPOSALS_CHARS = new char[] { '.' };
    private static final char[] VALID_INFO_CHARS = new char[] { '(', ',' };
    
    /**
     * A very simple context which invalidates information after typing several
     * chars.
     */
    private static class KotlinContextValidator implements IContextInformationValidator {
        private int initialOffset;
        
        @Override
        public void install(IContextInformation info, ITextViewer viewer, int offset) {
            this.initialOffset = offset;
        }

        @Override
        public boolean isContextInformationValid(int offset) {
            return Math.abs(initialOffset - offset) < 1;
        }
    }
    
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        String fileText = viewer.getDocument().get();
        int identOffset = getIdentifierStartOffset(fileText, offset);
        Assert.isTrue(identOffset <= offset);
        
        String identifierPart = fileText.substring(identOffset, offset);
        
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        
        //Keywords
        proposals.addAll(generateKeywordProposals(viewer, identOffset, offset, identifierPart));
        
        return proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    /**
     * Generate list of matching keywords
     * 
     * @param viewer the viewer whose document is used to compute the proposals
     * @param identOffset an offset within the document for which completions should be computed 
     * @param offset current position id the document
     * @param identifierPart part of current keyword
     * @return a collection of matching keywords  
     */
    private Collection<? extends ICompletionProposal> generateKeywordProposals(ITextViewer viewer, int identOffset,
            int offset, String identifierPart) {
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        if (!identifierPart.isEmpty()) {
            if (identOffset == 0 || Character.isWhitespace(viewer.getDocument().get().charAt(identOffset - 1))) {
                for (String keyword : KeywordManager.getAllKeywords()) {
                    if (keyword.startsWith(identifierPart)) {
                        proposals.add(new CompletionProposal(keyword, identOffset, offset - identOffset, keyword.length()));
                    }
                }
            }
        }
        return proposals;
    }

    /**
     * Method searches the beginning of the identifier 
     * 
     * @param text the text where search should be done.
     * @param offset 
     * @return offset of start symbol of identifier
     */
    private int getIdentifierStartOffset(String text, int offset) {
        int identStartOffset = offset;
        
        while ((identStartOffset != 0) && Character.isUnicodeIdentifierPart(text.charAt(identStartOffset - 1))) {
            identStartOffset--;
        }
        return identStartOffset;
    }
    
    
    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return VALID_PROPOSALS_CHARS;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return VALID_INFO_CHARS;
    }

    @Override
    public String getErrorMessage() {
        return "";
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return new KotlinContextValidator();
    }

    @Override
    public void assistSessionStarted(ContentAssistEvent event) {}

    @Override
    public void assistSessionEnded(ContentAssistEvent event) {}

    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {}

}
