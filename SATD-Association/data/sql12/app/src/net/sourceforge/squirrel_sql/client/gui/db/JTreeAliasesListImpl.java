package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.io.File;

public class JTreeAliasesListImpl implements IAliasesList, IAliasTreeInterface
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(JTreeAliasesListImpl.class);

   private static enum PasteMode
   {
      COPY, CUT;
   }


   JTree _tree = new JTree()
   {
      public String getToolTipText(MouseEvent event)
      {
         return JTreeAliasesListImpl.this.getToolTipText(event);    //To change body of overridden methods use File | Settings | File Templates.
      }
   };

   private JScrollPane _comp = new JScrollPane(_tree);
   private IApplication _app;
   private AliasesListModel _aliasesListModel;

   private TreePath[] _pathsToPaste;
   private PasteMode _pasteMode;

   private boolean _dontReactToAliasAdd = false ;


   public JTreeAliasesListImpl(IApplication app, AliasesListModel aliasesListModel)
   {
      _app = app;
      _aliasesListModel = aliasesListModel;
      _tree.setRootVisible(false);
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      root.removeAllChildren();
      _tree.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      _tree.setToolTipText("init");

      initRenderer();

      initDnD();


      _aliasesListModel.addListDataListener(new ListDataListener()
      {
         public void intervalAdded(ListDataEvent e)
         {
            onAliasAdded(e);
         }

         public void intervalRemoved(ListDataEvent e)
         {
            onAliasRemoved(e);
         }

         public void contentsChanged(ListDataEvent e)
         {
            onAliasChanged(e);
         }
      });


      _app.addApplicationListener(new ApplicationListener()
      {
         public void saveApplicationState()
         {
            onSaveApplicationState();
         }
      });

      initTree();

   }

   private void initRenderer()
   {
      DefaultTreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer()
      {
         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
         {
            return modifyRenderer(super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus), value);
         }
      };

      _tree.setCellRenderer(treeCellRenderer);


      AbstractAction cancelCutAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            if (null != _pathsToPaste && PasteMode.CUT.equals(_pasteMode))
            {
               _pathsToPaste = null;
               _tree.repaint();
            }
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      _tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "cancelCutAction");
      _tree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "cancelCutAction");
      _tree.getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "cancelCutAction");
      _tree.getActionMap().put("cancelCutAction", cancelCutAction);

   }

   private Component modifyRenderer(Component component, Object node)
   {
      JLabel ret = (JLabel) component;
      ret.setEnabled(true);


      if (null != _pathsToPaste && PasteMode.CUT.equals(_pasteMode))
      {
         DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;

         boolean found = false;
         for (TreePath treePath : _pathsToPaste)
         {
            if(treePath.getLastPathComponent() == dmtn)
            {
               found = true;
               break;
            }
         }
         ret.setEnabled(!found);
         ret.setDisabledIcon(ret.getIcon());
      }

      return component;
   }

   private void initDnD()
   {
      try
      {
         _tree.setDragEnabled(true);
         DropTarget dt = new DropTarget();

         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onDrop(dtde);
            }
         });

         _tree.setDropTarget(dt);
      }
      catch (TooManyListenersException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onDrop(DropTargetDropEvent dtde)
   {
      if(false == dtde.isLocalTransfer())
      {
         return;
      }

      TreePath targetPath = _tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

      TreePath[] toPaste = _tree.getSelectionPaths();

      if(0 != (DnDConstants.ACTION_COPY_OR_MOVE & dtde.getDropAction()))
      {
         execCut(toPaste, targetPath);
      }
   }

   private void initTree()
   {
      try
      {
         DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
         root.removeAllChildren();


         File file = new ApplicationFiles().getDatabaseAliasesTreeStructureFile();

         if(file.exists())
         {
            XMLBeanReader rdr = new XMLBeanReader();
            rdr.load(file);
            AliasFolderState rootState = (AliasFolderState) rdr.iterator().next();

            applyAliasFolderState(root, rootState);
         }
         else
         {
            for (int i = 0; i < _aliasesListModel.size(); i++)
            {
               root.add(new DefaultMutableTreeNode(_aliasesListModel.get(i)));
            }
            treeModel.nodeStructureChanged(root);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void applyAliasFolderState(DefaultMutableTreeNode rootNode, AliasFolderState rootState)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();

      for (AliasFolderState aliasFolderState : rootState.getKids())
      {
         aliasFolderState.applyNodes(rootNode, _aliasesListModel);
      }

      ArrayList<SQLAlias> unknownAliases = new ArrayList<SQLAlias>();
      for (int i = 0; i < _aliasesListModel.size(); i++)
      {
         SQLAlias sqlAlias = (SQLAlias) _aliasesListModel.get(i);
         if(null == findNode(sqlAlias, rootNode))
         {
            unknownAliases.add(sqlAlias);
         }
      }

      for (SQLAlias alias : unknownAliases)
      {
         rootNode.add(new DefaultMutableTreeNode(alias));
      }
      treeModel.nodeStructureChanged(rootNode);

      for (AliasFolderState aliasFolderState : rootState.getKids())
      {
         aliasFolderState.applyExpansionAndSelection(_tree);
      }
   }

   private void onSaveApplicationState()
   {
      try
      {
         DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         AliasFolderState state = new AliasFolderState(root, _tree);

         XMLBeanWriter wrt = new XMLBeanWriter(state);
         wrt.save(new ApplicationFiles().getDatabaseAliasesTreeStructureFile());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onAliasChanged(ListDataEvent e)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      SQLAlias changedAlias = (SQLAlias) _aliasesListModel.get(e.getIndex0());

      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

      DefaultMutableTreeNode node = findNode(changedAlias, root);
      treeModel.nodeChanged(node);
   }

   private DefaultMutableTreeNode findNode(SQLAlias sqlAlias, DefaultMutableTreeNode tn)
   {
      if(sqlAlias.equals(tn.getUserObject()))
      {
         return tn;
      }

      for (int i = 0; i < tn.getChildCount(); i++)
      {
         DefaultMutableTreeNode ret = findNode(sqlAlias, (DefaultMutableTreeNode) tn.getChildAt(i));
         if(null != ret)
         {
            return ret;
         }
      }

      return null;
   }

   private void onAliasRemoved(ListDataEvent e)
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();

      DefaultMutableTreeNode delNode = findRemovedNode();

      DefaultMutableTreeNode nextToSel;
      nextToSel = delNode.getNextSibling();

      if(null == nextToSel)
      {
         nextToSel = delNode.getPreviousSibling();
      }

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) delNode.getParent();
      treeModel.removeNodeFromParent(delNode);
      treeModel.nodeStructureChanged(parent);

      if(null != nextToSel)
      {
         _tree.setSelectionPath(new TreePath(nextToSel.getPath()));
      }
      else
      {
         if(parent != _tree.getModel().getRoot())
         {
            _tree.setSelectionPath(new TreePath(parent.getPath()));
         }
      }
   }

   private DefaultMutableTreeNode findRemovedNode()
   {
      ArrayList<SQLAlias> buf = new ArrayList<SQLAlias>();

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      fillAllAliasesFrom(root, buf);

      for (SQLAlias sqlAlias : buf)
      {
         if(-1 == _aliasesListModel.getIndex(sqlAlias))
         {
            return findNode(sqlAlias, root);
         }
      }

      return null;
   }

   private void fillAllAliasesFrom(DefaultMutableTreeNode node, ArrayList<SQLAlias> toFill)
   {
      if(node.getUserObject() instanceof SQLAlias)
      {
         toFill.add((SQLAlias) node.getUserObject());
      }
      else
      {
         for (int i = 0; i < node.getChildCount(); i++)
         {
              fillAllAliasesFrom((DefaultMutableTreeNode) node.getChildAt(i), toFill);
         }
      }
   }

   private void onAliasAdded(ListDataEvent e)
   {
      if(_dontReactToAliasAdd)
      {
         return;
      }

      SQLAlias newAlias = (SQLAlias) _aliasesListModel.get(e.getIndex0());
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newAlias);

      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      TreePath selPath = _tree.getSelectionPath();

      if(null == selPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
         root.add(newNode);
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
         if(selNode.getUserObject() instanceof SQLAlias)
         {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selPath.getParentPath().getLastPathComponent();

            int formerSilblingIx = treeModel.getIndexOfChild(parentNode, selNode);
            treeModel.insertNodeInto(newNode, parentNode, formerSilblingIx + 1);
         }
         else
         {
            selNode.add(newNode);
         }
      }

      treeModel.nodeStructureChanged((DefaultMutableTreeNode)treeModel.getRoot());

      _tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(newNode)));
   }

   public SQLAlias getSelectedAlias()
   {
      TreePath path = _tree.getSelectionPath();

      if(null == path)
      {
         return null;
      }

      if(false == path.getLastPathComponent() instanceof DefaultMutableTreeNode)
      {
         return null;
      }

      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();

      if(false == tn.getUserObject() instanceof ISQLAlias)
      {
         return null;
      }

      return (SQLAlias) tn.getUserObject();

   }

   public void sortAliases()
   {
      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
      AliasFolderState state = new AliasFolderState(root, _tree);

      state.sort();

      root.removeAllChildren();

      applyAliasFolderState(root, state);
   }

   public void requestFocus()
   {
      _tree.requestFocus();
   }

   public void deleteSelected()
   {
      TreePath[] selectionPaths = _tree.getSelectionPaths();

      if(1 == selectionPaths.length)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();
         TreeNode parent = selNode.getParent();

         if(selNode.getUserObject() instanceof SQLAlias)
         {
            SQLAlias toDel = (SQLAlias) selNode.getUserObject();
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDelete", toDel.getName())))
            {
               removeAlias(toDel);
            }
         }
         else
         {
            if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDeleteFolder", selNode.getUserObject())))
            {
               removeAllAliasesFromNode(selNode);

               selNode.removeFromParent();
               DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
               dtm.nodeStructureChanged(parent);
            }
         }
      }
      else if(1 < selectionPaths.length)
      {
         if (Dialogs.showYesNo(_app.getMainFrame(), s_stringMgr.getString("JTreeAliasesListImpl.confirmDeleteMultible")))
         {
            for (TreePath selectionPath : selectionPaths)
            {
               DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
               TreeNode parent = selNode.getParent();

               if(selNode.getUserObject() instanceof SQLAlias)
               {
                  SQLAlias toDel = (SQLAlias) selNode.getUserObject();
                 removeAlias(toDel);
               }
               else
               {
                  removeAllAliasesFromNode(selNode);
                  selNode.removeFromParent();
                  DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
                  dtm.nodeStructureChanged(parent);
               }
            }
         }
      }
   }

   public void modifySelected()
   {
      TreePath selPath = _tree.getSelectionPath();

      if(null == selPath)
      {
         return;
      }

      DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

      if(selNode.getUserObject() instanceof SQLAlias)
      {
         _app.getWindowManager().showModifyAliasInternalFrame((ISQLAlias) selNode.getUserObject());
      }
      else
      {
         String title = s_stringMgr.getString("JTreeAliasesListImpl.EditAliasFolderDlgTitle");
         String text = s_stringMgr.getString("JTreeAliasesListImpl.EditAliasFolderDlgText");
         EditAliasFolderDlg dlg = new EditAliasFolderDlg(_app.getMainFrame(), title, text, selNode.getUserObject().toString());

         GUIUtils.centerWithinParent(dlg);

         dlg.setVisible(true);

         String folderName = dlg.getFolderName();

         if(null == folderName)
         {
            return;
         }

         selNode.setUserObject(folderName);

         DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
         treeModel.nodeChanged(selNode);
      }
   }

   public boolean isEmpty()
   {
      return 0 == _aliasesListModel.getSize();
   }

   private void removeAllAliasesFromNode(DefaultMutableTreeNode selNode)
   {
      if(selNode.getUserObject() instanceof SQLAlias)
      {
         SQLAlias toDel = (SQLAlias) selNode.getUserObject();
         removeAlias(toDel);

      }
      else
      {
         ArrayList<DefaultMutableTreeNode> buf = new ArrayList<DefaultMutableTreeNode>();

         for (int i = 0; i < selNode.getChildCount(); i++)
         {
            buf.add((DefaultMutableTreeNode) selNode.getChildAt(i));
         }

         for (DefaultMutableTreeNode defaultMutableTreeNode : buf)
         {
            removeAllAliasesFromNode(defaultMutableTreeNode);
         }
      }
   }

   private void removeAlias(SQLAlias toDel)
   {
      _aliasesListModel.remove(_aliasesListModel.getIndex(toDel));
      _app.getDataCache().removeAlias(toDel);
   }

   public void selectListEntryAtPoint(Point point)
   {
      TreePath path = _tree.getPathForLocation(point.x, point.y);

      if(null != path)
      {
         _tree.setSelectionPath(path);
      }
   }

   public JComponent getComponent()
   {
      return _comp;
   }

   public void addMouseListener(MouseListener mouseListener)
   {
      _tree.addMouseListener(mouseListener);
   }

   public void removeMouseListener(MouseListener mouseListener)
   {
      _tree.removeMouseListener(mouseListener);
   }


   public String getToolTipText(MouseEvent evt)
   {
      TreePath path = _tree.getPathForLocation(evt.getPoint().x, evt.getPoint().y);

      if(null == path)
      {
         return null;
      }

      if(false == path.getLastPathComponent() instanceof DefaultMutableTreeNode)
      {
         return null;
      }

      Object userObj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

      if(false == userObj instanceof ISQLAlias)
      {
         return null;
      }

      return ((ISQLAlias)userObj).getName();
   }

   public void createNewFolder()
   {
      String title = s_stringMgr.getString("JTreeAliasesListImpl.NewAliasFolderDlgTitle");
      String text = s_stringMgr.getString("JTreeAliasesListImpl.NewAliasFolderDlgText");
      EditAliasFolderDlg dlg = new EditAliasFolderDlg(_app.getMainFrame(), title, text, null);
      GUIUtils.centerWithinParent(dlg);

      dlg.setVisible(true);

      String folderName = dlg.getFolderName();

      if(null == folderName)
      {
         return;
      }


      DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel();
      TreePath selPath = _tree.getSelectionPath();

      DefaultMutableTreeNode newFolder = AliasesTreeUtil.createFolderNode(folderName);


      if(null != selPath)
      {
         DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

         if(tn.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tn.getParent();
            parent.insert(newFolder, parent.getIndex(tn) + 1);
            treeModel.nodeStructureChanged(parent);
         }
         else
         {
            tn.add(newFolder);
            treeModel.nodeStructureChanged(tn);
         }
      }
      else
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) _tree.getModel().getRoot();
         root.add(newFolder);
         treeModel.nodeStructureChanged(root);
      }

      //_tree.expandPath(new TreePath(newFolder.getPath()));
      _tree.setSelectionPath(new TreePath(newFolder.getPath()));
      
   }

   public void cutSelected()
   {
      _pathsToPaste = _tree.getSelectionPaths();
      _pasteMode = PasteMode.CUT;
      _tree.repaint();
   }

   public void pasteSelected()
   {
      try
      {
         if (null == _pathsToPaste)
         {
            return;
         }

         switch (_pasteMode)
         {
            case COPY:
               execCopyToPaste(_pathsToPaste, _tree.getSelectionPath());
               break;
            case CUT:
               execCut(_pathsToPaste, _tree.getSelectionPath());
               break;
         }
      }
      finally
      {
         _pathsToPaste = null;
      }
   }

   private void execCopyToPaste(TreePath[] pathsToPaste, TreePath targetPath)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      DefaultMutableTreeNode[] copiedNodes = new DefaultMutableTreeNode[pathsToPaste.length];

      for (int i = 0; i < pathsToPaste.length; i++)
      {
         copiedNodes[i] = createCopy((DefaultMutableTreeNode) pathsToPaste[i].getLastPathComponent());
      }


      if (null == targetPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         for (int i = 0; i < copiedNodes.length; i++)
         {
            root.add(copiedNodes[i]);
         }
         dtm.nodeStructureChanged(root);
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

         if (selNode.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
            for (int i = 0; i < copiedNodes.length; i++)
            {
               parent.insert(copiedNodes[i], parent.getIndex(selNode) + 1);
            }
            dtm.nodeStructureChanged(parent);

         }
         else
         {
            for (int i = 0; i < copiedNodes.length; i++)
            {
               selNode.add(copiedNodes[i]);
            }
            dtm.nodeStructureChanged(selNode);
         }
      }

      TreePath[] newSelPaths = new TreePath[copiedNodes.length];
      for (int i = 0; i < newSelPaths.length; i++)
      {
         newSelPaths[i] = new TreePath(copiedNodes[i].getPath());
      }
      _tree.setSelectionPaths(newSelPaths);
   }

   private DefaultMutableTreeNode createCopy(DefaultMutableTreeNode nodeToCopy)
   {
      try
      {
         if(nodeToCopy.getUserObject() instanceof SQLAlias)
         {
            SQLAlias source = (SQLAlias) nodeToCopy.getUserObject();
            final IIdentifierFactory factory = IdentifierFactory.getInstance();
            SQLAlias newAlias = _app.getDataCache().createAlias(factory.createIdentifier());
            newAlias.assignFrom(source, false);

            try
            {
               _dontReactToAliasAdd = true;
               _app.getDataCache().addAlias(newAlias);
            }
            finally
            {
               _dontReactToAliasAdd = false;
            }
            return new DefaultMutableTreeNode(newAlias);
         }
         else
         {
            DefaultMutableTreeNode ret = AliasesTreeUtil.createFolderNode((String) nodeToCopy.getUserObject());

            for (int i = 0; i < nodeToCopy.getChildCount(); i++)
            {
               ret.add(createCopy((DefaultMutableTreeNode) nodeToCopy.getChildAt(i)));
            }
            return ret;
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void execCut(TreePath[] pathsToPaste, TreePath targetPath)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<DefaultMutableTreeNode>();

      for (int i = 0; i < pathsToPaste.length; i++)
      {
         if(false == pathsToPaste[i].equals(targetPath))
         {
            DefaultMutableTreeNode cutNode = (DefaultMutableTreeNode) pathsToPaste[i].getLastPathComponent();
            cutNodes.add(cutNode);
            dtm.removeNodeFromParent(cutNode);
         }
      }

      if (null == targetPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         for (int i = 0; i < cutNodes.size(); i++)
         {
            root.add(cutNodes.get(i));
         }
         dtm.nodeStructureChanged(root);
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

         if (selNode.isLeaf())
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
            for (int i = 0; i < cutNodes.size(); i++)
            {
               parent.insert(cutNodes.get(i), parent.getIndex(selNode) + 1);
            }
            dtm.nodeStructureChanged(parent);

         }
         else
         {
            for (int i = 0; i < cutNodes.size(); i++)
            {
               selNode.add(cutNodes.get(i));
            }
            dtm.nodeStructureChanged(selNode);
         }
      }

      TreePath[] newSelPaths = new TreePath[cutNodes.size()];
      for (int i = 0; i < newSelPaths.length; i++)
      {
         newSelPaths[i] = new TreePath(cutNodes.get(i).getPath());
      }
      _tree.setSelectionPaths(newSelPaths);
   }

   public void copyToPasteSelected()
   {
      _pathsToPaste = _tree.getSelectionPaths();
      _pasteMode = PasteMode.COPY;
   }

   public void collapseAll()
   {
      for (int i = 0; i < _tree.getRowCount(); i++)
      {
         _tree.collapseRow(i);
      }
   }

   public void expandAll()
   {
      for (int i = 0; i < _tree.getRowCount(); i++)
      {
         _tree.expandRow(i);
      }
   }

}
