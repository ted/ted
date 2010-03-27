package nu.ted.gwt.client.page;

import java.util.List;

import nu.ted.gwt.domain.SearchSeriesInfo;
import nu.ted.gwt.domain.FoundSeries;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {
    public List<FoundSeries> search(String filter);
    public SearchSeriesInfo getSeriesInfo(String searchUUID);
}
