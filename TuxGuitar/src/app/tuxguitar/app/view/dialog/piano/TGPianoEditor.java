package app.tuxguitar.app.view.dialog.piano;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import app.tuxguitar.app.editor.TGExternalBeatViewerManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.tools.scale.ScaleEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPianoEditor implements TGEventListener{

	private TGContext context;
	private TGPiano piano;
    private boolean visible;

	public TGPianoEditor(TGContext context){
        this.context = context;
        this.appendListeners();
    }

	public TGPiano getPiano(){
		return this.piano;
	}

    public boolean isVisible(){
        return (getPiano() != null && !getPiano().isDisposed() && this.visible);
    }

    public void hidePiano(){
        this.visible = false;
        getPiano().setVisible(this.visible);

        TGEditorManager.getInstance(this.context).removeRedrawListener(this);
        TGExternalBeatViewerManager.getInstance(this.context).removeBeatViewerListener(this);

        TGWindow tgWindow = TGWindow.getInstance(this.context);
        tgWindow.getWindow().layout();
    }

    public void showPiano(){
        this.visible = true;
        getPiano().setVisible(this.visible);
        getPiano().computePackedSize();

        TGEditorManager.getInstance(this.context).addRedrawListener(this);
        TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);

        TGWindow tgWindow = TGWindow.getInstance(this.context);
        tgWindow.getWindow().layout();
    }

	public void createPiano(UIPanel parent, boolean visible) {
		this.piano = new TGPiano(this.context, parent);
        this.piano.setVisible(visible);

        UITableLayout uiLayout = (UITableLayout) parent.getLayout();
        uiLayout.set(this.piano.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

        if( visible ) {
            this.showPiano();
        }
	}

	public void appendListeners(){
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);
	}

    public void dispose(){
        if( getPiano() != null && !getPiano().isDisposed()){
            getPiano().dispose();
        }
    }

    public void redraw(){
        if( getPiano() != null && !getPiano().isDisposed() /*&& !TuxGuitar.getInstance().isLocked()*/){
            getPiano().redraw();
        }
    }

    public void redrawPlayingMode(){
        if( getPiano() != null && !getPiano().isDisposed() && !TuxGuitar.getInstance().isLocked()){
            getPiano().redrawPlayingMode();
        }
    }

    public void loadProperties(){
        if( getPiano() != null && !getPiano().isDisposed()){
            getPiano().loadProperties();
        }
    }

    public void loadIcons(){
        if( getPiano() != null && !getPiano().isDisposed()){
            getPiano().loadIcons();
        }
    }

    public void loadScale(){
        if( getPiano() != null){
            getPiano().loadScale();
        }
    }

    public void showExternalBeat(TGBeat beat) {
        if(getPiano() != null && !getPiano().isDisposed()){
            getPiano().setExternalBeat(beat);
        }
    }

    public void hideExternalBeat() {
        if(getPiano() != null && !getPiano().isDisposed()){
            getPiano().setExternalBeat(null);
        }
    }

	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}

	public void processExternalBeatEvent(TGEvent event) {
		if( TGExternalBeatViewerEvent.ACTION_SHOW.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.showExternalBeat((TGBeat) event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_BEAT));
		}
		else if( TGExternalBeatViewerEvent.ACTION_HIDE.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.hideExternalBeat();
		}
	}

	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processRedrawEvent(event);
				}
				else if( TGExternalBeatViewerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processExternalBeatEvent(event);
				}
				else if( ScaleEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadScale();
				}
			}
		});
	}

	public static TGPianoEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPianoEditor.class.getName(), new TGSingletonFactory<TGPianoEditor>() {
			public TGPianoEditor createInstance(TGContext context) {
				return new TGPianoEditor(context);
			}
		});
	}
}
