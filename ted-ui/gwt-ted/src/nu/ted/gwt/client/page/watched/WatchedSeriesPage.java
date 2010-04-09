package nu.ted.gwt.client.page.watched;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import net.bugsquat.gwtsite.client.page.PageId;
import nu.ted.gwt.client.Css;
import nu.ted.gwt.client.TedPageId;
import nu.ted.gwt.client.image.Images;
import nu.ted.gwt.client.page.DefaultPage;
import nu.ted.gwt.domain.GwtWatchedSeries;

public class WatchedSeriesPage extends DefaultPage {

	private WatchedSeriesPageController controller;
	private FlowPanel watchedSeriesList;
	private Map<Short, FlowPanel> watchedUIDToPanel;

	public WatchedSeriesPage(WatchedSeriesPageController controller) {
		this.controller = controller;

		watchedSeriesList = new FlowPanel();
		content.add(watchedSeriesList);

		watchedUIDToPanel = new HashMap<Short, FlowPanel>();
	}

	@Override
	public String getHeaderText() {
		return "Watched Series";
	}

	@Override
	public PageId getId() {
		return TedPageId.WATCHED_SERIES;
	}

	public void setWatchedSeries(List<GwtWatchedSeries> watchedSeries) {
		clearPage();

		if (watchedSeries.isEmpty()) {
			showNoWatchedSeriesMessage();
			return;
		}

		for (final GwtWatchedSeries watched : watchedSeries) {
			addWatchedSeriesPanel(watched);
		}
	}

	/**
	 * If the series exists, remove it from the list.
	 *
	 * @param getuID the UID of the series to be removed.
	 */
	public void removeSeriesFromList(Short seriesUID) {
		if (watchedUIDToPanel.containsKey(seriesUID)) {
			watchedUIDToPanel.get(seriesUID).removeFromParent();
			watchedUIDToPanel.remove(seriesUID);

			if (watchedUIDToPanel.isEmpty()) {
				clearPage();
				showNoWatchedSeriesMessage();
			}
		}
	}

	public boolean isShowingSeries(short seriesUid) {
		return watchedUIDToPanel.containsKey(seriesUid);
	}

	public void addSeriesToList(GwtWatchedSeries watched) {
		if (!isShowingSeries(watched.getuID())) {

			if (watchedUIDToPanel.isEmpty()) {
				clearPage();
			}

			addWatchedSeriesPanel(watched);
		}
	}

	private void addWatchedSeriesPanel(GwtWatchedSeries watched) {
		FlowPanel watchedSeriesPanel = createWatchedSeriesPanel(watched);
		watchedUIDToPanel.put(watched.getuID(), watchedSeriesPanel);
		watchedSeriesList.add(watchedSeriesPanel);
	}

	private void showNoWatchedSeriesMessage() {
		watchedSeriesList.add(new Label("There are currently no series being watched."));
	}

	private FlowPanel createWatchedSeriesPanel(final GwtWatchedSeries watched) {
		String thumbImgKey = watched.getName() + watched.getuID();
		String thumbUrl = GWT.getModuleBaseURL() + "images?iid=" + thumbImgKey;
		FlowPanel imagePanel = new FlowPanel();
		imagePanel.setStyleName(Css.WatchedSeriesPage.WATCHED_SERIES_IMAGE);
		imagePanel.add(new Image(thumbUrl));

		Label info = new Label(watched.getName());
		info.setStyleName(Css.WatchedSeriesPage.WATCHED_SERIES_INFO);

		Image stopWatchingImage = new Image(Images.INSTANCE.stopWatchingIcon());
		stopWatchingImage.setStyleName(Css.WatchedSeriesPage.WATCHED_SERIES_ACTION_ITEM);
		stopWatchingImage.setTitle("Stop Watching");
		stopWatchingImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				controller.stopWatching(watched.getuID());
			}

		});

		FlowPanel actionPanel = new FlowPanel();
		actionPanel.setStyleName(Css.WatchedSeriesPage.WATCHED_SERIES_ACTIONS);
		actionPanel.add(stopWatchingImage);

		FlowPanel watchingPanel = new FlowPanel();
		watchingPanel.setStyleName(Css.WatchedSeriesPage.WATCHED_SERIES);
		watchingPanel.add(actionPanel);
		watchingPanel.add(info);
		watchingPanel.add(imagePanel);

		return watchingPanel;
	}

	private void clearPage() {
		this.watchedUIDToPanel.clear();
		this.watchedSeriesList.clear();
	}
}
