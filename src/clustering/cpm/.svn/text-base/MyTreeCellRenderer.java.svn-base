package clustering.cpm;

import java.awt.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * Render the tree cells 
 * @author Dequan
 *
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer
{

    public MyTreeCellRenderer()
    {
    }

    public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pIsSelected, boolean pIsExpanded, boolean pIsLeaf, int pRow, boolean pHasFocus)
    {
        MyTreeNode node = (MyTreeNode)pValue;
        DefaultTreeCellRenderer c = (DefaultTreeCellRenderer)super.getTreeCellRendererComponent(pTree, pValue, pIsSelected, pIsExpanded, pIsLeaf, pRow, pHasFocus);
        if(node.getFlag())
        {
            Font f = c.getFont();
            c.setFont(f.deriveFont(2));
            if(!pIsSelected)
                c.setTextNonSelectionColor(Color.gray);
            else
                c.setTextSelectionColor(Color.gray);
        } else
        {
            Font f = c.getFont();
            c.setFont(f.deriveFont(0));
            if(!pIsSelected)
                c.setTextNonSelectionColor(Color.black);
            else
                c.setTextSelectionColor(Color.black);
        }
        return c;
    }
}
