/*
 * generated by Xtext
 */
package fr.lip6.move.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GalUiModule extends fr.lip6.move.ui.AbstractGalUiModule {
	public GalUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
		return CommentMapper.class;
	}
}
