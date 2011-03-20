/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.ldap.model.schema.MutableAttributeTypeImpl;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.alias.Alias;
import org.apache.directory.studio.schemaeditor.model.alias.AliasWithError;
import org.apache.directory.studio.schemaeditor.model.alias.AliasesStringParser;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.AttributeTypeSelectionDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.EditAliasesDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.ObjectClassSelectionDialog;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingObjectClass;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class is the Overview Page of the Object Class Editor
 */
public class ObjectClassEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = ObjectClassEditor.ID + "overviewPage"; //$NON-NLS-1$

    /** The original object class */
    private ObjectClass originalObjectClass;

    /** The modified object class */
    private ObjectClass modifiedObjectClass;

    /** The original schema */
    private Schema originalSchema;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaHandler Listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( MutableAttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( MutableAttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( MutableAttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleAdded(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleAdded( MatchingRule mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleModified(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleModified( MatchingRule mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleRemoved(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleRemoved( MatchingRule mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClass oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClass oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClass oc )
        {
            if ( !oc.equals( originalObjectClass ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaAdded( Schema schema )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaRemoved( Schema schema )
        {
            if ( !schema.equals( originalSchema ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxAdded(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxAdded( LdapSyntax syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxModified(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxModified( LdapSyntax syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxRemoved(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxRemoved( LdapSyntax syntax )
        {
            refreshUI();
        }
    };

    // UI fields
    private Text aliasesText;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Table superiorsTable;
    private TableViewer superiorsTableViewer;
    private Button addButtonSuperiorsTable;
    private Button removeButtonSuperiorsTable;
    private Combo classTypeCombo;
    private Button obsoleteCheckbox;
    private Table mandatoryAttributesTable;
    private TableViewer mandatoryAttributesTableViewer;
    private Button addButtonMandatoryTable;
    private Button removeButtonMandatoryTable;
    private Table optionalAttributesTable;
    private TableViewer optionalAttributesTableViewer;
    private Button addButtonOptionalTable;
    private Button removeButtonOptionalTable;

    // Listeners

    /** The listener for the Aliases Text Widget */
    private ModifyListener aliasesTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            AliasesStringParser parser = new AliasesStringParser();
            parser.parse( aliasesText.getText() );
            List<Alias> parsedAliases = parser.getAliases();
            modifiedObjectClass.setNames( new String[0] );
            List<String> aliasesList = new ArrayList<String>();
            for ( Alias parsedAlias : parsedAliases )
            {
                if ( !( parsedAlias instanceof AliasWithError ) )
                {
                    aliasesList.add( parsedAlias.getAlias() );
                }
            }
            modifiedObjectClass.setNames( aliasesList.toArray( new String[0] ) );
            setEditorDirty();
        }
    };

    /** The listener for Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            EditAliasesDialog editDialog = new EditAliasesDialog( modifiedObjectClass.getNames() );
            if ( editDialog.open() != Window.OK )
            {
                return;
            }
            if ( editDialog.isDirty() )
            {
                modifiedObjectClass.setNames( editDialog.getAliases() );
                if ( ( modifiedObjectClass.getNames() != null ) && ( modifiedObjectClass.getNames().size() != 0 ) )
                {
                    aliasesText.setText( ViewUtils.concateAliases( modifiedObjectClass.getNames() ) );
                }
                else
                {
                    aliasesText.setText( "" ); //$NON-NLS-1$
                }
                setEditorDirty();
            }
        }
    };

    /** The Modify listener for the OID Text Widget */
    private ModifyListener oidTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            oidText.setForeground( ViewUtils.COLOR_BLACK );
            oidText.setToolTipText( "" ); //$NON-NLS-1$

            String oid = oidText.getText();

            if ( OID.isOID( oid ) )
            {
                if ( ( originalObjectClass.getOid().equals( oid ) )
                    || !( schemaHandler.isAliasOrOidAlreadyTaken( oid ) ) )
                {
                    modifiedObjectClass.setOid( oid );
                    setEditorDirty();
                }
                else
                {
                    oidText.setForeground( ViewUtils.COLOR_RED );
                    oidText.setToolTipText( Messages.getString( "ObjectClassEditorOverviewPage.ElementOIDExists" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                oidText.setForeground( ViewUtils.COLOR_RED );
                oidText.setToolTipText( Messages.getString( "ObjectClassEditorOverviewPage.MalformedOID" ) ); //$NON-NLS-1$
            }
        }
    };

    /** The Verify listener for the OID Text Widget */
    private VerifyListener oidTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };

    /** The listener for the Schema Hyperlink Widget*/
    private HyperlinkAdapter schemaLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SchemaEditorInput input = new SchemaEditorInput( schemaHandler.getSchema( modifiedObjectClass
                .getSchemaName() ) );
            String editorId = SchemaEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };

    /** The listener for Description Text Widget */
    private ModifyListener descriptionTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            int caretPosition = descriptionText.getCaretPosition();
            modifiedObjectClass.setDescription( descriptionText.getText() );
            descriptionText.setSelection( caretPosition );
            setEditorDirty();
        }
    };

    /** The listener for Class Type Widget */
    private ModifyListener classTypeListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( classTypeCombo.getSelectionIndex() == 0 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.ABSTRACT );
            }
            else if ( classTypeCombo.getSelectionIndex() == 1 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.AUXILIARY );
            }
            else if ( classTypeCombo.getSelectionIndex() == 2 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.STRUCTURAL );
            }
            setEditorDirty();
        }
    };

    /** The listener for Obsolete Widget */
    private SelectionAdapter obsoleteListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedObjectClass.setObsolete( obsoleteCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for Mandatory Attributes Table Widget */
    private MouseListener mandatoryAttributesTableListener = new MouseListener()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof MutableAttributeTypeImpl )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( MutableAttributeTypeImpl ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseDown( MouseEvent e )
        {
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Mandatory Attributes section */
    private SelectionAdapter addButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<MutableAttributeTypeImpl> hiddenATs = new ArrayList<MutableAttributeTypeImpl>();
            List<String> mustsHidden = modifiedObjectClass.getMustAttributeTypeOids();
            if ( mustsHidden != null )
            {
                for ( String must : mustsHidden )
                {
                    MutableAttributeTypeImpl at = schemaHandler.getAttributeType( must );
                    if ( at != null )
                    {
                        hiddenATs.add( at );
                    }
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new MutableAttributeTypeImpl[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            MutableAttributeTypeImpl at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMusts = new ArrayList<String>();
            List<String> musts = modifiedObjectClass.getMustAttributeTypeOids();
            if ( musts != null )
            {
                for ( String must : musts )
                {
                    newMusts.add( must );
                }
            }
            List<String> names = at.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                newMusts.add( names.get( 0 ) );
            }
            else
            {
                newMusts.add( at.getOid() );
            }
            modifiedObjectClass.setMustAttributeTypeOids( newMusts );

            fillInMandatoryAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Mandatory Attributes section */
    private SelectionAdapter removeButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMusts = new ArrayList<String>();
                List<String> musts = modifiedObjectClass.getMustAttributeTypeOids();
                for ( String must : musts )
                {
                    newMusts.add( must );
                }

                if ( selectedElement instanceof MutableAttributeTypeImpl )
                {
                    for ( String name : ( ( MutableAttributeTypeImpl ) selectedElement ).getNames() )
                    {
                        newMusts.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMusts.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMustAttributeTypeOids( newMusts );

                fillInMandatoryAttributesTable();
                addButtonMandatoryTable.setFocus();
                removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Optional Attributes Table Widget */
    private MouseListener optionalAttributesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof MutableAttributeTypeImpl )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( MutableAttributeTypeImpl ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Optional Attributes section */
    private SelectionAdapter addButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<MutableAttributeTypeImpl> hiddenATs = new ArrayList<MutableAttributeTypeImpl>();
            List<String> maysHidden = modifiedObjectClass.getMayAttributeTypeOids();
            if ( maysHidden != null )
            {
                for ( String may : maysHidden )
                {
                    MutableAttributeTypeImpl at = schemaHandler.getAttributeType( may );
                    if ( at != null )
                    {
                        hiddenATs.add( at );
                    }
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new MutableAttributeTypeImpl[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            MutableAttributeTypeImpl at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMays = new ArrayList<String>();
            List<String> mays = modifiedObjectClass.getMayAttributeTypeOids();
            if ( mays != null )
            {
                for ( String may : mays )
                {
                    newMays.add( may );
                }
            }
            List<String> names = at.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                newMays.add( names.get( 0 ) );
            }
            else
            {
                newMays.add( at.getOid() );
            }
            modifiedObjectClass.setMayAttributeTypeOids( newMays );

            fillInOptionalAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Optional Attributes section */
    private SelectionAdapter removeButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMays = new ArrayList<String>();
                List<String> mays = modifiedObjectClass.getMayAttributeTypeOids();
                for ( String may : mays )
                {
                    newMays.add( may );
                }

                if ( selectedElement instanceof MutableAttributeTypeImpl )
                {
                    for ( String name : ( ( MutableAttributeTypeImpl ) selectedElement ).getNames() )
                    {
                        newMays.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMays.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMayAttributeTypeOids( newMays );

                fillInOptionalAttributesTable();
                addButtonOptionalTable.setFocus();
                removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Superiors Table Widget */
    private MouseListener superiorsTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof ObjectClass )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new ObjectClassEditorInput( ( ObjectClass ) selectedElement ),
                            ObjectClassEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Superiors Table */
    private SelectionAdapter addButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ObjectClassSelectionDialog dialog = new ObjectClassSelectionDialog();
            List<ObjectClass> hiddenOCs = new ArrayList<ObjectClass>();
            for ( String sup : modifiedObjectClass.getSuperiorOids() )
            {
                ObjectClass oc = schemaHandler.getObjectClass( sup );
                if ( oc != null )
                {
                    hiddenOCs.add( oc );
                }
            }
            hiddenOCs.add( originalObjectClass );
            dialog.setHiddenObjectClasses( hiddenOCs.toArray( new ObjectClass[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            ObjectClass oc = dialog.getSelectedObjectClass();
            if ( oc == null )
            {
                return;
            }

            List<String> superiors = new ArrayList<String>();
            List<String> sups = modifiedObjectClass.getSuperiorOids();
            for ( String sup : sups )
            {
                superiors.add( sup );
            }
            List<String> names = oc.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                superiors.add( names.get( 0 ) );
            }
            else
            {
                superiors.add( oc.getOid() );
            }
            modifiedObjectClass.setSuperiorOids( superiors );

            fillInSuperiorsTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Superiors Table */
    private SelectionAdapter removeButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> superiors = new ArrayList<String>();
                List<String> sups = modifiedObjectClass.getSuperiorOids();
                for ( String sup : sups )
                {
                    superiors.add( sup );
                }

                if ( selectedElement instanceof ObjectClass )
                {
                    for ( String name : ( ( ObjectClass ) selectedElement ).getNames() )
                    {
                        superiors.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingObjectClass )
                {
                    superiors.remove( ( ( NonExistingObjectClass ) selectedElement ).getName() );
                }

                modifiedObjectClass.setSuperiorOids( superiors );

                fillInSuperiorsTable();
                addButtonSuperiorsTable.setFocus();
                removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The filter listener for Mouse Wheel events */
    private Listener mouseWheelFilter = new Listener()
    {
        public void handleEvent( Event event )
        {
            // Hiding Mouse Wheel events for Combo widgets
            if ( event.widget instanceof Combo )
            {
                event.doit = false;
            }
        }
    };


    /**
     * Default constructor
     * @param editor
     *      the associated editor
     */
    public ObjectClassEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, Messages.getString( "ObjectClassEditorOverviewPage.Overview" ) ); //$NON-NLS-1$
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.addListener( schemaHandlerListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the original and modified object classes
        modifiedObjectClass = ( ( ObjectClassEditor ) getEditor() ).getModifiedObjectClass();
        originalObjectClass = ( ( ObjectClassEditor ) getEditor() ).getOriginalObjectClass();
        originalSchema = schemaHandler.getSchema( originalObjectClass.getSchemaName() );

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.getBody().setLayout( new GridLayout() );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        Composite bottomComposite = toolkit.createComposite( form.getBody() );
        bottomComposite.setLayout( new GridLayout( 2, true ) );
        bottomComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Mandatory Attributes Section
        createMandatoryAttributesSection( bottomComposite, toolkit );

        // Optionnal Attributes Section
        createOptionalAttributesSection( bottomComposite, toolkit );

        // Enabling or disabling the fields
        //        setFieldsEditableState();

        // Filling the UI with values from the object class
        fillInUiFields();

        // Listeners initialization
        addListeners();

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form,
            PluginConstants.PLUGIN_ID + "." + "object_class_editor" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Creates the General Information Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createGeneralInformationSection( Composite parent, FormToolkit toolkit )
    {
        // General Information Section
        Section section_general_information = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        section_general_information.setDescription( Messages
            .getString( "ObjectClassEditorOverviewPage.SpecifyGeneralInformation" ) ); //$NON-NLS-1$
        section_general_information.setText( Messages.getString( "ObjectClassEditorOverviewPage.GeneralInformation" ) ); //$NON-NLS-1$
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        client_general_information.setLayout( new GridLayout( 2, false ) );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Adding elements to the section

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        Composite aliasComposite = toolkit.createComposite( client_general_information );
        GridLayout aliasCompositeGridLayout = new GridLayout( 2, false );
        toolkit.paintBordersFor( aliasComposite );
        aliasCompositeGridLayout.marginHeight = 1;
        aliasCompositeGridLayout.marginWidth = 1;
        aliasComposite.setLayout( aliasCompositeGridLayout );
        aliasComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        aliasesText = toolkit.createText( aliasComposite, "" ); //$NON-NLS-1$
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        aliasesButton = toolkit.createButton( aliasComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.EditAliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Schema" ), SWT.WRAP ); //$NON-NLS-1$
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SUPERIORS Table
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.SuperiorClasses" ) ); //$NON-NLS-1$
        Composite superiorsComposite = toolkit.createComposite( client_general_information );
        GridLayout superiorsCompositeGridLayout = new GridLayout( 2, false );
        toolkit.paintBordersFor( superiorsComposite );
        superiorsCompositeGridLayout.marginHeight = 1;
        superiorsCompositeGridLayout.marginWidth = 1;
        superiorsComposite.setLayout( superiorsCompositeGridLayout );
        superiorsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        superiorsTable = toolkit.createTable( superiorsComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        toolkit.paintBordersFor( superiorsTable );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        gridData.heightHint = 45;
        gridData.minimumHeight = 45;
        superiorsTable.setLayoutData( gridData );
        superiorsTableViewer = new TableViewer( superiorsTable );
        superiorsTableViewer.setContentProvider( new ObjectClassEditorSuperiorsTableContentProvider() );
        superiorsTableViewer.setLabelProvider( new ObjectClassEditorSuperiorsTableLabelProvider() );
        addButtonSuperiorsTable = toolkit.createButton( superiorsComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        addButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        removeButtonSuperiorsTable = toolkit.createButton( superiorsComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        removeButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        // CLASS TYPE Combo
        toolkit
            .createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.ClassType" ) ); //$NON-NLS-1$
        classTypeCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        classTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initClassTypeCombo();

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Mandatory Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createMandatoryAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // MANDATORY ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassEditorOverviewPage.MandatoryAttributes" ) ); //$NON-NLS-1$
        section.setDescription( Messages.getString( "ObjectClassEditorOverviewPage.SpecifyMandatoryAttributes" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        mandatoryAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        mandatoryAttributesTable.setLayoutData( gd );
        mandatoryAttributesTableViewer = new TableViewer( mandatoryAttributesTable );
        mandatoryAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        mandatoryAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

        addButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        removeButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonMandatoryTable.setLayoutData( gd );
        removeButtonMandatoryTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonMandatoryTable.setEnabled( false );
    }


    /**
     * Creates the Optional Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createOptionalAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // OPTIONAL ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassEditorOverviewPage.OptionalAttributes" ) ); //$NON-NLS-1$
        section.setDescription( Messages.getString( "ObjectClassEditorOverviewPage.SpecifyOptionalAttributes" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        optionalAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        optionalAttributesTable.setLayoutData( gd );
        optionalAttributesTableViewer = new TableViewer( optionalAttributesTable );
        optionalAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        optionalAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

        addButtonOptionalTable = toolkit.createButton( client,
            Messages.getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        removeButtonOptionalTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonOptionalTable.setLayoutData( gd );
        removeButtonOptionalTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonOptionalTable.setEnabled( false );
    }


    /**
     * Initializes the UI fields from the input.
     */
    private void fillInUiFields()
    {
        // ALIASES Label
        if ( ( modifiedObjectClass.getNames() != null ) && ( modifiedObjectClass.getNames().size() != 0 ) )
        {
            aliasesText.setText( ViewUtils.concateAliases( modifiedObjectClass.getNames() ) );
        }
        else
        {
            aliasesText.setText( "" ); //$NON-NLS-1$
        }

        // OID Field
        if ( modifiedObjectClass.getOid() != null )
        {
            oidText.setText( modifiedObjectClass.getOid() );
        }

        schemaLabel.setText( modifiedObjectClass.getSchemaName() );

        // DESCRIPTION Field
        if ( modifiedObjectClass.getDescription() != null )
        {
            descriptionText.setText( modifiedObjectClass.getDescription() );
        }

        // SUPERIORS Table
        fillInSuperiorsTable();

        // CLASS TYPE Combo
        fillInClassType();

        // OBSOLETE Checkbox
        obsoleteCheckbox.setSelection( modifiedObjectClass.isObsolete() );

        // MANDATORY ATTRIBUTES Table
        fillInMandatoryAttributesTable();

        // OPTIONNAL ATTRIBUTES Table
        fillInOptionalAttributesTable();
    }


    /**
     * Fills in the Superiors Table.
     */
    private void fillInSuperiorsTable()
    {
        if ( modifiedObjectClass.getSuperiorOids() != null )
        {
            superiorsTableViewer.setInput( modifiedObjectClass.getSuperiorOids() );
        }
    }


    /**
     * Initializes the Class Type Combo
     */
    private void initClassTypeCombo()
    {
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Abstract" ), 0 ); //$NON-NLS-1$
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Auxiliary" ), 1 ); //$NON-NLS-1$
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Structural" ), 2 ); //$NON-NLS-1$
    }


    /**
     * Fills in the Class Type Combo
     */
    private void fillInClassType()
    {
        if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.ABSTRACT )
        {
            classTypeCombo.select( 0 );
        }
        else if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.AUXILIARY )
        {
            classTypeCombo.select( 1 );
        }
        else if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.STRUCTURAL )
        {
            classTypeCombo.select( 2 );
        }
    }


    /**
     * Initializes the Mandatory Attributes Table
     */
    private void fillInMandatoryAttributesTable()
    {
        if ( modifiedObjectClass.getMustAttributeTypeOids() != null )
        {
            mandatoryAttributesTableViewer.setInput( modifiedObjectClass.getMustAttributeTypeOids() );
        }
    }


    /**
     * Initializes the Optional Attributes Table
     */
    private void fillInOptionalAttributesTable()
    {
        if ( modifiedObjectClass.getMayAttributeTypeOids() != null )
        {
            optionalAttributesTableViewer.setInput( modifiedObjectClass.getMayAttributeTypeOids() );
        }
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        aliasesText.addModifyListener( aliasesTextModifyListener );
        aliasesButton.addSelectionListener( aliasesButtonListener );
        oidText.addModifyListener( oidTextModifyListener );
        oidText.addVerifyListener( oidTextVerifyListener );
        descriptionText.addModifyListener( descriptionTextListener );
        addButtonSuperiorsTable.addSelectionListener( addButtonSuperiorsTableListener );
        removeButtonSuperiorsTable.addSelectionListener( removeButtonSuperiorsTableListener );
        classTypeCombo.addModifyListener( classTypeListener );
        obsoleteCheckbox.addSelectionListener( obsoleteListener );
        addButtonMandatoryTable.addSelectionListener( addButtonMandatoryTableListener );
        removeButtonMandatoryTable.addSelectionListener( removeButtonMandatoryTableListener );
        addButtonOptionalTable.addSelectionListener( addButtonOptionalTableListener );
        removeButtonOptionalTable.addSelectionListener( removeButtonOptionalTableListener );
        schemaLink.addHyperlinkListener( schemaLinkListener );
        superiorsTable.addMouseListener( superiorsTableListener );
        mandatoryAttributesTable.addMouseListener( mandatoryAttributesTableListener );
        optionalAttributesTable.addMouseListener( optionalAttributesTableListener );

        Display.getCurrent().addFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        aliasesText.removeModifyListener( aliasesTextModifyListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        superiorsTable.removeMouseListener( superiorsTableListener );
        addButtonSuperiorsTable.removeSelectionListener( addButtonSuperiorsTableListener );
        removeButtonSuperiorsTable.removeSelectionListener( removeButtonSuperiorsTableListener );
        classTypeCombo.removeModifyListener( classTypeListener );
        obsoleteCheckbox.removeSelectionListener( obsoleteListener );
        mandatoryAttributesTable.removeMouseListener( mandatoryAttributesTableListener );
        addButtonMandatoryTable.removeSelectionListener( addButtonMandatoryTableListener );
        removeButtonMandatoryTable.removeSelectionListener( removeButtonMandatoryTableListener );
        optionalAttributesTable.removeMouseListener( optionalAttributesTableListener );
        addButtonOptionalTable.removeSelectionListener( addButtonOptionalTableListener );
        removeButtonOptionalTable.removeSelectionListener( removeButtonOptionalTableListener );

        Display.getCurrent().removeFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Sets the editor as dirty
     */
    private void setEditorDirty()
    {
        ( ( ObjectClassEditor ) getEditor() ).setDirty( true );
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        removeListeners();
        fillInUiFields();
        addListeners();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#dispose()
     */
    public void dispose()
    {
        schemaHandler.removeListener( schemaHandlerListener );

        super.dispose();
    }
}
