package edu.arizona.biosemantics.oto.steps.client.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderCategory;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderSet;

public interface OrderServiceAsync {

	void getOrderSets(String uploadID,
			AsyncCallback<ArrayList<OrderSet>> callback);

	void saveOrderSet(OrderSet orserSet, AsyncCallback<Void> callback);

	void getOrderCategories(String uploadID,
			AsyncCallback<ArrayList<OrderCategory>> callback);

	void getOrderSetByID(String categoryID, AsyncCallback<OrderSet> callback);

}
