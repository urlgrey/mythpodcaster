/*
 * TranscodingProfileSubscriptionsPanel.java
 *
 * Created: Jun 23, 2010
 *
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.client;

import java.util.List;

import net.urlgrey.mythpodcaster.client.service.UIControllerService;
import net.urlgrey.mythpodcaster.client.service.UIControllerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author scottkidder
 *
 */
public class TranscodingProfileSubscriptionsPanel extends Composite {

	private RecordingsPanel parent;
	private VerticalPanel panel = new VerticalPanel();
	private VerticalPanel subscriptionsPanel = new VerticalPanel();
	private Label subscriptionsLabel = new Label("Subscriptions:");
	private Grid subscriptionsTable = new Grid();
	private Button addSubscriptionButton = new Button("Add Subscription");
	private String applicationUrl = null;
	private String seriesId = null;
	private String seriesTitle = null;
	private AddSubscriptionHandler addSubscriptionHandler;

	/**
	 * 
	 */
	public TranscodingProfileSubscriptionsPanel(RecordingsPanel parent) {
		this.parent = parent;
		subscriptionsTable.resizeColumns(4);
		subscriptionsTable.setStyleName("mythpodcaster-SubscriptionsTable");

		subscriptionsPanel.add(subscriptionsLabel);
		subscriptionsPanel.add(subscriptionsTable);
		subscriptionsLabel.setVisible(false);
		subscriptionsTable.setVisible(false);

		subscriptionsPanel.setStyleName("mythpodcaster-SubscriptionsPanel");
		addSubscriptionHandler = new AddSubscriptionHandler(parent);
		addSubscriptionButton.addClickHandler(addSubscriptionHandler);

		panel.add(subscriptionsPanel);
		panel.add(addSubscriptionButton);
		initWidget(panel);
	}

	public void update(String seriesId, String seriesTitle) {
		this.seriesId = seriesId;
		this.seriesTitle = seriesTitle;

		this.addSubscriptionHandler.setSeriesId(seriesId);
		this.addSubscriptionHandler.setSeriesTitle(seriesTitle);

		refreshData();
	}

	public void refreshData() {
		if (this.applicationUrl == null) {
			UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
			try {
				service.retrieveApplicationUrl(new AsyncCallback<String>() {

					@Override
					public void onSuccess(String arg0) {
						applicationUrl = arg0;
					}

					@Override
					public void onFailure(Throwable arg0) {

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		AsyncCallback<List<FeedSubscriptionItemDTO>> callback = new AsyncCallback<List<FeedSubscriptionItemDTO>>() {


			@Override
			public void onSuccess(List<FeedSubscriptionItemDTO> subscriptions) {
				subscriptionsTable.resizeRows(subscriptions.size());

				int i = 0;
				for (FeedSubscriptionItemDTO item : subscriptions) {
					subscriptionsTable.setText(i, 0, item.getTranscodeProfileDisplayName());
					subscriptionsTable.setWidget(i, 1, new HTML("<a target=_blank href=\"" + applicationUrl + "/" + item.getTranscodeProfile() + "/" + seriesId + ".rss\">Feed</a>"));
					subscriptionsTable.setWidget(i, 2, new HTML("<a target=_blank href=\"" + applicationUrl + "/" + item.getTranscodeProfile() + "/" + seriesId + ".html\">HTML</a>"));
					final Button unsubscribeButton = new Button("Unsubscribe");
					unsubscribeButton.addClickHandler(new UnsubscribeHandler(parent, seriesId, item.getTranscodeProfile()));
					subscriptionsTable.setWidget(i++, 3, unsubscribeButton);
				}

				if (subscriptions.size() > 0) {
					subscriptionsLabel.setVisible(true);
				}
				subscriptionsTable.setVisible(true);
			}

			@Override
			public void onFailure(Throwable arg0) {

			}
		};

		subscriptionsTable.clear();
		subscriptionsLabel.setVisible(false);
		subscriptionsTable.setVisible(false);

		UIControllerServiceAsync service = (UIControllerServiceAsync) GWT.create(UIControllerService.class);
		try {
			service.findSubscriptionsForSeries(seriesId, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
