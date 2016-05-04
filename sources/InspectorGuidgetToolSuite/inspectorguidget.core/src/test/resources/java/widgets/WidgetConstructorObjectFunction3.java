package foo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;

import java.util.Collections;
import java.util.Collection;

class Foo {
	final Group node;

	Foo() {
		node = new Group();
		getter().forEach(c -> node.getChildren().add(c));
	}

	protected Collection<Node> getter() {
		return Collections.singleton(new RadioButton());
	}
}
