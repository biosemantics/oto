package edu.arizona.biosemantics.oto.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TermOrder implements Serializable {
	
	private List<Order> orders;
	private Authentication authentication;
	
	public TermOrder(){ }
	
	public TermOrder(List<Order> orders, Authentication authentication) {
		this.orders = orders;
		this.authentication = authentication;
	}
	
	public Authentication getAuthentication(){
		return authentication;
	}
	
	public void setAuthentication(Authentication authentication){
		this.authentication = authentication;
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
}
