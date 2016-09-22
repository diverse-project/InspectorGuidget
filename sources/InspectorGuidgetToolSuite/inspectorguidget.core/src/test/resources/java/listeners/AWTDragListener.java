package foo;

import java.awt.dnd.*;

class DragGesture implements DragGestureListener{
	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {

	}
}

class DragSource implements DragSourceListener{
	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {

	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {

	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {

	}

	@Override
	public void dragExit(final DragSourceEvent dse) {

	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {

	}
}

class DragSourceMotion implements DragSourceMotionListener {
	@Override
	public void dragMouseMoved(final DragSourceDragEvent dsde) {

	}
}

class DropTarget implements DropTargetListener {
	@Override
	public void dragEnter(final DropTargetDragEvent dtde) {

	}

	@Override
	public void dragOver(final DropTargetDragEvent dtde) {

	}

	@Override
	public void dropActionChanged(final DropTargetDragEvent dtde) {

	}

	@Override
	public void dragExit(final DropTargetEvent dte) {

	}

	@Override
	public void drop(final DropTargetDropEvent dtde) {

	}
}
