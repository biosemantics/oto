package edu.arizona.biosemantics.oto.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TermOrder implements Serializable {
	
	@XmlRootElement
	public static class Result implements Serializable {
		private int count;
		
		public Result() { 
			
		}
		
		public Result(int count) {
			this.count = count;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
	
	private List<Order> orders;
	private String authenticationToken;
	private boolean replace;
	
	public TermOrder(){ }
	
	public TermOrder(List<Order> orders, String authenticationToken, boolean replace) {
		this.orders = orders;
		this.authenticationToken = authenticationToken;
		this.replace = replace;
	}
	
	public String getAuthenticationToken(){
		return authenticationToken;
	}
	
	public void setAuthenticationToken(String authenticationToken){
		this.authenticationToken = authenticationToken;
	}
	
	public List<Order> getOrders(){
		return orders;
	}
	
	public void setOrders(List<Order> orders){
		this.orders = orders;
	}
	
	public void addEntry(String orderName, ArrayList<String> includedTerms){
		orders.add(new Order(orderName, includedTerms));
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("TermOrderData {\n");
		for (Order pair: orders){
			buff.append("\t[" + pair + "]\n");
		}
		buff.append("}\n");
		return buff.toString();
	}
	
	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}
}
