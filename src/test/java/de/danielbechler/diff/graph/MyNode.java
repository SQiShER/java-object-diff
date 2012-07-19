package de.danielbechler.diff.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyNode {

	private int id = -1;
	private MyNode parent = null;	
	private List<MyNode> children = new ArrayList<MyNode>();
	private MyNode directReference = null;
	private Map<String, Object> map = new HashMap<String, Object>();
	private String value = null;

	public MyNode() {
		super();
	}

	public MyNode(MyNode parent) {
		super();
		this.parent = parent;
		this.parent.getChildren().add(this);
	}

	public MyNode(int id, MyNode parent, String value) {
		super();
		this.id = id;
		this.parent = parent;
		this.parent.getChildren().add(this);
		this.value = value;
	}

	public MyNode(String value) {
		super();
		this.value = value;
	}

	public MyNode(int id) {
		super();
		this.id = id;
	}

	public MyNode(int id, String value) {
		super();
		this.id = id;
		this.value = value;
	}
	
	public MyNode getParent() {
		return parent;
	}

	public void setParent(MyNode parent) {
		this.parent = parent;
	}

	public MyNode getDirectReference() {
		return directReference;
	}

	public void setDirectReference(MyNode directReference) {
		this.directReference = directReference;
	}

	public List<MyNode> getChildren() {
		return children;
	}

	public void setChildren(List<MyNode> children) {
		this.children = children;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyNode other = (MyNode) obj;		
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "[id=" + id + "]";
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	
}
