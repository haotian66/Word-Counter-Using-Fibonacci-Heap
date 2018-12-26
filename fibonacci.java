import java.io.*;
import java.util.*;

public class fibonacci{

    node maxroot = null; //When we instantiate a fibonacci heap, it is empty, so the initial max root is null.

    public void insert(node n){   //function for insert a node in the top-level list
        if (maxroot == null){
            maxroot = n;
        }
        else {
            if (maxroot.right != null){ // if there is a node on the right of max root, insert the new node between this node and max root
                node temp = maxroot.right;
                maxroot.right = n;
                n.right = temp;
                n.left = maxroot;
                temp.left = n;
            }
            else{   //If max root has no sibling, make the new node as the only sibling of max root, they must form a circle.
                maxroot.right = n;
                n.left = maxroot;
                maxroot.left = n;
                n.right = maxroot;
            }
            if (n.value > maxroot.value){  //adjust max root pointer if the new node value is bigger than max root
                maxroot = n;
            }
        }
    }

    public void increasekey(node n,int v){
        n.value = n.value + v; //increase key
        if ((n.parent != null) && (n.value > n.parent.value)){
            //if the increased node has a parent and its current value is greater than its parent's value, cut the node from its sibling list
            node t = n.parent;
            n.right.left = n.left;
            n.left.right = n.right;
            n.parent  = null;
            n.childcut = false; // top-level roots have false childcut
            t.degree = t.degree -1; // decrease parent degree by 1
            if (t.children == n){
                if (n.right != null && n.right != n){ //Parent only links to one child. If the parent is linked to the increased node, and there are siblings, link parent to the siblings
                    t.children = n.right;
                }
                else{
                    t.children  = null;
                }
            }
            boolean cuttemp = t.childcut;
            t.childcut = true; //set parent childcut to true
            insert(n);  //insert the increased node to top-level list

            while (t != null && cuttemp == true) {  //perform cascade cut using the same process
                n = t;
                t = t.parent;
                n.right.left = n.left;
                n.left.right = n.right;
                n.parent = null;
                n.childcut = false;
                t.degree = t.degree -1;
                if (t.children == n) {
                    if (n.right != null && n.right != n) {
                        t.children = n.right;
                    } else {
                        t.children = null;
                    }
                }
                cuttemp = t.childcut;
                t.childcut = true;
                insert(n);
            }
        }
        if (n.value > maxroot.value){
            maxroot = n;
        }
    }



    public node removemax(){
        node m = maxroot; // store the current max root for returning later
        if (m != null){
            node c = m.children;
            int i = 0;
            while (i < m.degree){
                node t = c.right;  //store the sibling for cutting in the next iteration
                c.left.right = c.right;//cut all children of the max root from the sibling list
                c.right.left = c.left;
                c.parent = null;
                c.childcut = false;
                insert(c);//and insert them to top-level list
                c = t;
                i = i+1;
            }
            m.left.right = m.right; //remove max root
            m.right.left = m.left;
            if (m.right != null && m.right != m){ //check whether there are other nodes in the top-level list, if there are any, they are the children of the removed max, we should perform pairwise combine
                maxroot = m.right;
                combine();
            } else{ //If there are no nodes in the top-level list, the whole heap is empty, set max root to null
                maxroot = null;
            }
            return m;
        }
        return null;
    }


    public void combine(){  //function for pairwise combine

        if (maxroot != null){
            List<node> list = new ArrayList<>();  //this list is the degree table storing degrees of the trees
            int j = 0;
            while (j < 10000){
                list.add(null);
                j = j+ 1;
            }
            node m = maxroot;
            int number = 0;
            if(m!=null){
                do{
                    m = m.right;
                    number = number +1;   //count the number of trees waiting to be combined
                }while(m!= null && m != maxroot);
            }
            int i = 0;
            while (i<number){		//for each max tree
                node next = m.right;					//store a pointer pointing to next tree
                while(list.get(m.degree)!= null){  // If we already have a tree in the degree table, then merge
                    node t = list.get(m.degree);
                    if(t.value > m.value){   // make sure m points to the bigger root of two trees
                        node temp = t;
                        t = m;
                        m = temp;
                    }
                    t.left.right = t.right;  //remove the tree with smaller root from its sibling list
                    t.right.left = t.left;
                    t.parent = m;						//combine
                    if(m.children == null){				//if the bigger root has no child, set smaller root as its child
                        m.children = t;
                        t.left = t;
                        t.right = t;
                    } else{								//if bigger root has children, then insert the smaller root between its first and second child
                        t.left = m.children;
                        t.right = m.children.right;
                        m.children.right.left = t;
                        m.children.right = t;
                    }
                    list.set(m.degree,null); //after combining, the original degree in the table should be empty, and the degree of the combined tree should increase by 1
                    t.childcut = false;
                    m.degree += 1;
                }
                list.set(m.degree,m);				// the combined tree should also be put into degree table
                m = next;
                i = i + 1;
            }
            maxroot = null;
            i = 0;
            while (i<10000){  //insert all the trees in the degree table to the empty top-level list
                if (list.get(i) != null){
                    if (maxroot == null){
                        maxroot = list.get(i);
                    } else{
                        node k = list.get(i);
                        k.left.right = k.right;
                        k.right.left = k.left;
                        insert(k);
                    }
                }
                i = i + 1;
            }
        }
    }
}
