/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.main.browsemode;

import java.security.AccessControlException;

import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.frame.ViewController;
import org.freeplane.core.ui.components.FreeplaneToolBar;
import org.freeplane.core.util.LogUtils;
import org.freeplane.features.browsemode.BModeController;
import org.freeplane.features.browsemode.BToolbarContributor;
import org.freeplane.features.common.attribute.AttributeController;
import org.freeplane.features.common.clipboard.ClipboardController;
import org.freeplane.features.common.cloud.CloudController;
import org.freeplane.features.common.edge.EdgeController;
import org.freeplane.features.common.encrypt.EncryptionController;
import org.freeplane.features.common.filter.FilterController;
import org.freeplane.features.common.icon.IconController;
import org.freeplane.features.common.link.LinkController;
import org.freeplane.features.common.map.MapController;
import org.freeplane.features.common.map.SummaryNode;
import org.freeplane.features.common.map.UnfoldAll;
import org.freeplane.features.common.nodelocation.LocationController;
import org.freeplane.features.common.nodestyle.NodeStyleController;
import org.freeplane.features.common.note.NoteController;
import org.freeplane.features.common.styles.LogicalStyleController;
import org.freeplane.features.common.styles.MapStyle;
import org.freeplane.features.common.text.TextController;
import org.freeplane.features.common.url.UrlManager;
import org.freeplane.view.swing.addins.filepreview.ViewerController;
import org.freeplane.view.swing.ui.UserInputListenerFactory;

/**
 * @author Dimitry Polivaev 24.11.2008
 */
public class BModeControllerFactory {
	private static BModeController modeController;

	static public BModeController createModeController() {
		final Controller controller = Controller.getCurrentController();
		modeController = new BModeController(controller);
		final UserInputListenerFactory userInputListenerFactory = new UserInputListenerFactory(modeController);
		modeController.setUserInputListenerFactory(userInputListenerFactory);
		controller.addModeController(modeController);
		controller.selectModeForBuild(modeController);
		modeController.setMapController(new MapController());
		UrlManager.install(new UrlManager());
		AttributeController.install(new AttributeController(modeController));
		IconController.install(new IconController(modeController));
		NodeStyleController.install(new NodeStyleController(modeController));
		EdgeController.install(new EdgeController(modeController));
		CloudController.install(new CloudController(modeController));
		NoteController.install(new NoteController());
		TextController.install(new TextController(modeController));
		LinkController.install(new LinkController());
		LogicalStyleController.install(new LogicalStyleController());
		try {
			ClipboardController.install(new ClipboardController());
		}
		catch (final AccessControlException e) {
			LogUtils.warn("can not access system clipboard, clipboard controller disabled");
		}
		LocationController.install(new LocationController());
		SummaryNode.install();
		new MapStyle(true);
		final BToolbarContributor toolbarContributor = new BToolbarContributor();
		modeController.addMenuContributor(toolbarContributor);
		controller.getMapViewManager().addMapViewChangeListener(toolbarContributor);
		userInputListenerFactory.setNodePopupMenu(new JPopupMenu());
		final FreeplaneToolBar toolBar = new FreeplaneToolBar("main_toolbar", SwingConstants.HORIZONTAL);
		toolBar.putClientProperty(ViewController.VISIBLE_PROPERTY_KEY, "toolbarVisible");
		userInputListenerFactory.addToolBar("/main_toolbar", ViewController.TOP, toolBar);
		userInputListenerFactory.addToolBar("/filter_toolbar", ViewController.TOP, FilterController.getController(
		    controller).getFilterToolbar());
		userInputListenerFactory.addToolBar("/status", ViewController.BOTTOM, controller.getViewController()
		    .getStatusBar());
		new UnfoldAll();
		new ViewerController();
		EncryptionController.install(new EncryptionController());
		return modeController;
	}
}
