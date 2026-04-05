package app.tuxguitar.editor.action.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGChangeTrackHideAction extends TGActionBase {

	public static final String NAME = "action.track.change-hide";

	public TGChangeTrackHideAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTrack track = ((TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));

		context.setAttribute(TGSetTrackHideAction.ATTRIBUTE_HIDE, Boolean.valueOf(!track.isHide()));

		TGActionManager.getInstance(getContext()).execute(TGSetTrackHideAction.NAME, context);
	}
}
