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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorAddPageAction;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorExportConfigurationAction;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorImportConfigurationAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ServerConfigurationEditorPage extends FormPage
{
    protected static final Color GRAY_COLOR = new Color( null, 120, 120, 120 );
    protected static final String TABULATION = "      ";

    private ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setEditorDirty();
        }
    };

    private SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public ServerConfigurationEditorPage( ServerConfigurationEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * Gets the ServerConfigurationEditor object associated with the page.
     *
     * @return
     *      the ServerConfigurationEditor object associated with the page
     */
    public ServerConfigurationEditor getServerConfigurationEditor()
    {
        return ( ServerConfigurationEditor ) getEditor();
    }


    /**
     * Sets the associated editor dirty.
     */
    private void setEditorDirty()
    {
        getServerConfigurationEditor().setDirty( true );
    }


    /**
     * Gets the configuration bean associated with the editor.
     *
     * @return
     *      the configuration bean associated with the editor
     */
    public ConfigBean getConfigBean()
    {
        ConfigBean configBean = getServerConfigurationEditor().getConfigBean();

        if ( configBean == null )
        {
            configBean = new ConfigBean();
            getServerConfigurationEditor().setConfigBean( configBean );
        }

        return configBean;
    }


    /**
     * Gets the directory service associated with the editor.
     *
     * @return
     *      the directory service bean associated with the editor
     */
    public DirectoryServiceBean getDirectoryServiceBean()
    {
        DirectoryServiceBean directoryServiceBean = getConfigBean().getDirectoryServiceBean();

        if ( directoryServiceBean == null )
        {
            directoryServiceBean = new DirectoryServiceBean();
            getConfigBean().addDirectoryService( directoryServiceBean );
        }

        return directoryServiceBean;
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( getTitle() );

        Composite parent = form.getBody();
        parent.setLayout( new GridLayout() );

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading( form.getForm() );

        IToolBarManager toolbarManager = form.getToolBarManager();
        toolbarManager.add( new EditorImportConfigurationAction() );
        toolbarManager.add( new Separator() );
        toolbarManager.add( new EditorExportConfigurationAction() );
        toolbarManager.add( new Separator() );
        toolbarManager.add( new EditorAddPageAction( ( ServerConfigurationEditor ) getEditor() ) );
        toolbarManager.update( true );

        createFormContent( parent, toolkit );
    }


    protected abstract void createFormContent( Composite parent, FormToolkit toolkit );


    /**
     * Creates a Text that can be used to enter a port number.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Text that can be used to enter a port number
     */
    protected Text createPortText( FormToolkit toolkit, Composite parent )
    {
        Text portText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 42;
        portText.setLayoutData( gd );
        portText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        portText.setTextLimit( 5 );

        return portText;
    }


    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Text that can be used to enter a port number
     */
    protected Text createIntegerText( FormToolkit toolkit, Composite parent )
    {
        Text integerText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        integerText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        return integerText;
    }


    /**
     * Creates default value Label.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @param text
     *      the text string
     * @return
     *      a default value Label
     */
    protected Label createDefaultValueLabel( FormToolkit toolkit, Composite parent, String text )
    {
        Label label = toolkit.createLabel( parent, NLS.bind( "(Default: {0})", text ) );
        label.setForeground( GRAY_COLOR );

        return label;
    }


    /**
     * Adds a 'dirty' listener to the given Text.
     *
     * @param text
     *      the Text control
     */
    public void addDirtyListener( Text text )
    {
        text.addModifyListener( dirtyModifyListener );
    }


    /**
     * Adds a 'dirty' listener to the given Button.
     *
     * @param button
     *      the Button control
     */
    public void addDirtyListener( Button button )
    {
        button.addSelectionListener( dirtySelectionListener );
    }
}