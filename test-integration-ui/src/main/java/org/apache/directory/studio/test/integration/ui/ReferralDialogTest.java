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

package org.apache.directory.studio.test.integration.ui;


import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swtbot.eclipse.finder.SWTEclipseBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


/**
 * Tests the referral dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralDialogTest extends AbstractServerTest
{
    private SWTEclipseBot bot;
    private Connection connection;


    protected void setUp() throws Exception
    {
        super.setUp();
        bot = new SWTEclipseBot();
        SWTBotUtils.openLdapPerspective( bot );
        connection = SWTBotUtils.createTestConnection( bot, "ReferralDialogTest", ldapService.getPort() );
    }


    protected void tearDown() throws Exception
    {
        SWTBotUtils.deleteTestConnections();
        bot = null;
        super.tearDown();
    }


    /**
     * Test for DIRSTUDIO-343.
     * 
     * Follows a continuation reference.
     * 
     * @throws Exception
     *             the exception
     */
    public void testBrowseAndFollowContinuationReference() throws Exception
    {
        // ensure that referrals handling method is FOLLOW
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // select ou=system, don't expand yet
        final SWTBotTreeItem systemNode = SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE",
            "ou=system" );

        // expand ou=system, that reads the referral and opens the referral
        // dialog
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
        {
            public void run()
            {
                systemNode.expand();
            }
        } );
        bot.sleep( 1000 );

        // click OK in the referral dialog
        bot.button( "OK" ).click();
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // ensure that the referral URL and target is visible
        SWTBotTreeItem referralNode = systemNode.getNode( "ldap://localhost:" + ldapService.getPort()
            + "/ou=users,ou=system" );
        assertNotNull( referralNode );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "ldap://localhost:"
            + ldapService.getPort() + "/ou=users,ou=system" );

    }


    /**
     * Test for DIRSTUDIO-343.
     * 
     * Does not follow a continuation reference by clicking the cancel button in
     * the referral dialog.
     * 
     * @throws Exception
     *             the exception
     */
    public void testBrowseAndCancelFollowingContinuationReference() throws Exception
    {
        // ensure that referrals handling method is FOLLOW
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.FOLLOW.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        final SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );

        // select ou=system, don't expand yet
        final SWTBotTreeItem systemNode = SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE",
            "ou=system" );

        // expand ou=system, that reads the referral and opens the referral
        // dialog
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
        {
            public void run()
            {
                systemNode.expand();
            }
        } );
        bot.sleep( 1000 );

        // click Cancel in the referral dialog
        bot.button( "Cancel" ).click();
        SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // ensure that the referral URL and target is not visible
        SWTBotTreeItem referralNode = null;
        try
        {
            referralNode = systemNode.getNode( "ldap://localhost:" + ldapService.getPort() + "/ou=users,ou=system" );
        }
        catch ( WidgetNotFoundException wnfe )
        {
            // that is expected
        }
        assertNull( referralNode );
    }


    /**
     * Tests ignore referral by setting the connection property to IGNORE.
     * 
     * @throws Exception
     *             the exception
     */
    public void testBrowseAndIgnoreReferral() throws Exception
    {
        // ensure that referrals handling method is IGNORE
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.IGNORE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.IGNORE.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        // expand ou=system, the referral must be ignored, no referral dialog
        // expected
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotTreeItem systemNode = SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // ensure that the referral entry is not visible
        SWTBotTreeItem referralNode1 = null;
        try
        {
            referralNode1 = systemNode.getNode( "ldap://localhost:" + ldapService.getPort() + "/ou=users,ou=system" );
        }
        catch ( WidgetNotFoundException wnfe )
        {
            // that is expected
        }
        assertNull( referralNode1 );
        SWTBotTreeItem referralNode2 = null;
        try
        {
            referralNode2 = systemNode.getNode( "cn=referralDialogTest" );
        }
        catch ( WidgetNotFoundException wnfe )
        {
            // that is expected
        }
        assertNull( referralNode2 );
    }


    /**
     * Tests manage referral entry by setting the connection property to MANAGE.
     * 
     * @throws Exception
     *             the exception
     */
    public void testBrowseAndManageReferralEntry() throws Exception
    {
        // ensure that referrals handling method is MANAGE
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD, ReferralHandlingMethod.MANAGE.ordinal() );
        int referralsHandlingMethodOrdinal = connection.getConnectionParameter().getExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD );
        assertEquals( ReferralHandlingMethod.MANAGE.ordinal(), referralsHandlingMethodOrdinal );

        // create the referral entry
        createReferralEntry();

        // expand ou=system, the referral is managed, no referral dialog
        // expected
        SWTBotTree browserTree = SWTBotUtils.getLdapBrowserTree( bot );
        SWTBotTreeItem systemNode = SWTBotUtils.selectEntry( bot, browserTree, true, "DIT", "Root DSE", "ou=system" );

        // ensure that the referral entry is visible
        SWTBotTreeItem referralNode = systemNode.getNode( "cn=referralDialogTest" );
        assertNotNull( referralNode );
        SWTBotUtils.selectEntry( bot, browserTree, false, "DIT", "Root DSE", "ou=system", "cn=referralDialogTest" );
    }


    private void createReferralEntry() throws Exception
    {
        ServerEntry entry = new DefaultServerEntry( rootDSE.getDirectoryService().getRegistries() );
        entry.setDn( new LdapDN( "cn=referralDialogTest,ou=system" ) );
        entry.add( "objectClass", "top", "referral", "extensibleObject" );
        entry.add( "cn", "referralDialogTest" );
        entry.add( "ref", "ldap://localhost:" + ldapService.getPort() + "/ou=users,ou=system" );
        rootDSE.add( entry );
    }
}
