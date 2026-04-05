package app.tuxguitar.editor.undo.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGUndoableTrackSoloMuteHide extends TGUndoableTrackBase {

	private int doAction;
	private int track;
	private boolean undoSolo;
	private boolean undoMute;
	private boolean undoHide;
	private boolean redoSolo;
	private boolean redoMute;
    private boolean redoHide;

	private TGUndoableTrackSoloMuteHide(TGContext context){
		super(context);
	}

	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		if( this.undoSolo != this.redoSolo ){
			this.setTrackSolo(actionContext, this.getTrack(this.track), this.redoSolo );
		}
		if( this.undoMute != this.redoMute ){
			this.setTrackMute(actionContext, this.getTrack(this.track), this.redoMute );
		}
        if( this.undoHide != this.redoHide ){
			this.setTrackHide(actionContext, this.getTrack(this.track), this.redoHide );
		}
		this.doAction = UNDO_ACTION;
	}

	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		if( this.undoSolo != this.redoSolo ){
			this.setTrackSolo(actionContext, this.getTrack(this.track), this.undoSolo );
		}
		if( this.undoMute != this.redoMute ){
			this.setTrackMute(actionContext, this.getTrack(this.track), this.undoMute );
		}
        if( this.undoHide != this.redoHide ){
            this.setTrackHide(actionContext, this.getTrack(this.track), this.undoHide );
        }
		this.doAction = REDO_ACTION;
	}

	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}

	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}

	public static TGUndoableTrackSoloMuteHide startUndo(TGContext context, TGTrack track){
		TGUndoableTrackSoloMuteHide undoable = new TGUndoableTrackSoloMuteHide(context);
		undoable.doAction = UNDO_ACTION;
		undoable.track = track.getNumber();
		undoable.undoSolo = track.isSolo();
		undoable.undoMute = track.isMute();
		undoable.undoHide = track.isHide();

		return undoable;
	}

	public TGUndoableTrackSoloMuteHide endUndo(TGTrack track){
		this.redoSolo = track.isSolo();
		this.redoMute = track.isMute();
		this.redoHide = track.isHide();

		return this;
	}

	public TGTrack getTrack(int number) {
		return getSongManager().getTrack(getSong(), number);
	}
}
