package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/

		this.root = buildTree(new TagNode("root", null, null)).firstChild;
	}

	/**
	 * 
	 * Insert into root
	 * 
	 * @param head
	 * @param tn
	 * @return
	 */
	

	private TagNode buildTree(TagNode parent) {
		if(!this.sc.hasNext()) {
			return parent;
		}
		String line = this.sc.nextLine();
		if(line.charAt(0)=='<') {
			if(line.charAt(1)=='/') {
				return parent;
			}
			String tag = line.substring(1, line.length()-1);
			insert(parent, buildTree(new TagNode(tag,null,null)));
		}else {
			insert(parent,new TagNode(line,null,null));
		}
		
		return buildTree(parent);
	}

	private TagNode insert(TagNode head, TagNode new_node) {
		if(head.firstChild == null) {
			head.firstChild = new_node;
			return new_node;
		}else {
			TagNode node = head.firstChild;
			while(node.sibling!=null) {
				node = node.sibling;
			}
			node.sibling = new_node;
			return new_node;
		}
		
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceTag(root, oldTag, newTag);

	}

	private void replaceTag(TagNode tmp, String oldTag, String newTag) {
		// TODO Auto-generated method stub
		if(tmp.tag.equals(oldTag)) {
			tmp.tag = newTag;
		}
		replaceTag(tmp.firstChild, oldTag, newTag);
		replaceTag(tmp.sibling, oldTag, newTag);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of this
	 * row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/

		// First find table tag
		TagNode table = findTag(root, "table");
		// Get tr tag
		TagNode tr = table.firstChild;

		// Loop untill row is found
		// Starts at 1 because first row is 1 not 0
		int i = 1;
		while (i < row) {
			tr = tr.sibling;
			i++;
		}

		// Loop through row to be bold by going through the siblings of that row tag node
		TagNode crntRow = tr.firstChild;
		while (crntRow != null) {
			// Bolds that row
			crntRow.firstChild = new TagNode("b", crntRow.firstChild, null);
			// Next column
			crntRow = crntRow.sibling;
		}
		
	}


	private TagNode findTag(TagNode head,String tag) {
		// TODO Auto-generated method stub
		
		if(head==null) return null;
		
		if(head.tag.equals(tag)) {
			return head;
		}
		TagNode childTag = findTag(head.firstChild,tag);
		if(childTag!=null) {
			return childTag;
		}
		TagNode siblingTag = findTag(head.sibling,tag);
		if(siblingTag!=null) {
			return siblingTag;
		}
		
		return null;
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b,
	 * all occurrences of the tag are removed. If the tag is ol or ul, then All
	 * occurrences of such a tag are removed from the tree, and, in addition, all
	 * the li tags immediately under the removed tag are converted to p tags.
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		removeTag(tag,root);
	}
	private TagNode removeTag(String tag, TagNode root) {
		if (root == null) {
			return null;
		}
	
		TagNode child = removeTag(tag, root.firstChild);
		TagNode sibling = removeTag(tag, root.sibling);

		if (tag.equals(root.tag)) {
			if (tag.equals("ol") || tag.equals("ul")) {
				TagNode currentTag = root.firstChild;
				for (currentTag = root.firstChild; currentTag != null; currentTag = currentTag.sibling) {
					if (currentTag.tag.equals("li")) {
						currentTag.tag = "p";
					}
				}
			}
			TagNode crnt = child;
			TagNode prev = null;
			for (crnt = child; crnt != null; crnt = crnt.sibling) {
				// Calls curr node to get next node
				
				prev = crnt;
			}
			// Calls sibling node to get the next nodes sibling
			prev.sibling = sibling;
			return child;
			// Else, go onto next sibling or child
		} else {
			root.sibling = sibling;
			root.firstChild = child;
			return root;
		}
	}

	

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag  Tag to be added
	 */
	public void addTag(String word, String tag) {
	}


	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes new
	 * lines, so that when it is printed, it will be identical to the input file
	 * from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("      ");
			}
			;
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level + 1);
			}
		}
	}
}