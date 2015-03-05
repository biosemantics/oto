package edu.arizona.biosemantics.oto.common.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TermOrder {
	private ArrayList<Order> orderData;
	private Login loginData;
	
	public TermOrder(){
		orderData = new ArrayList<Order>();
	}
	
	public Login getLoginData(){
		return loginData;
	}
	
	public void setLoginData(Login data){
		loginData = data;
	}
	
	public ArrayList<Order> getOrderData(){
		return orderData;
	}
	
	public void setOrderData(ArrayList<Order> data){
		this.orderData = data;
	}
	
	public void addEntry(String orderName, ArrayList<String> includedTerms){
		orderData.add(new Order(orderName, includedTerms));
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("TermOrderData {\n");
		for (Order pair: orderData){
			buff.append("\t[" + pair + "]\n");
		}
		buff.append("}\n");
		return buff.toString();
	}
}
