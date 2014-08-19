package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.steps.shared.rpc.OrderService;
import edu.arizona.biosemantics.oto.steps.server.db.OrderDAO;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderCategory;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderSet;

public class OrderServiceImpl extends RemoteServiceServlet implements
		OrderService {

	private static final long serialVersionUID = 6156458035937676513L;

	/**
	 * get the ordersets for order page when loading
	 * 
	 * @throws Exception
	 */
	@Override
	public ArrayList<OrderSet> getOrderSets(String uploadID) throws Exception {
		return OrderDAO.getInstance().getOrderSets(Integer.parseInt(uploadID));
	}

	/**
	 * save orderSet to database
	 * 
	 * @throws Exception
	 */
	@Override
	public void saveOrderSet(OrderSet orderSet) throws Exception {
		OrderDAO.getInstance().saveOrderSet(orderSet);
	}

	@Override
	public ArrayList<OrderCategory> getOrderCategories(String uploadID)
			throws Exception {
		return OrderDAO.getInstance().getOrderCategories(
				Integer.parseInt(uploadID));
	}

	@Override
	public OrderSet getOrderSetByID(String categoryID) throws Exception {
		return OrderDAO.getInstance().getOrderSetByID(
				Integer.parseInt(categoryID));
	}

}
