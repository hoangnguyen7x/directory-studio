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
package org.apache.directory.studio.schemaeditor.controller.actions;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.view.dialogs.RenameProjectDialog;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProjectWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This action launches the NewProjectWizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameProjectAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated viewer */
    private TableViewer viewer;

    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;


    /**
     * Creates a new instance of RenameProjectAction.
     *
     * @param view
     *      the associate view
     */
    public RenameProjectAction( TableViewer viewer )
    {
        super( Messages.getString( "RenameProjectAction.RenameProjectAction" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_RENAME_PROJECT );
        setActionDefinitionId( PluginConstants.CMD_RENAME_PROJECT );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_RENAME ) );
        setEnabled( false );
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                setEnabled( selection.size() == 1 );
            }
        } );
        projectsHandler = Activator.getDefault().getProjectsHandler();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
        {
            Project project = ( ( ProjectWrapper ) selection.getFirstElement() ).getProject();
            RenameProjectDialog dialog = new RenameProjectDialog( project.getName() );
            if ( dialog.open() == Dialog.OK )
            {
                projectsHandler.renameProject( project, dialog.getNewName() );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
