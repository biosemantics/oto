package edu.arizona.biosemantics.oto.common.ontologylookup.webservice;

/**
 * provided by the ontology lookup webservice
 * @author Fengqiong
 *
 */
public interface QueryService extends javax.xml.rpc.Service {
    public java.lang.String getOntologyQueryAddress();

    public Query getOntologyQuery() throws javax.xml.rpc.ServiceException;

    public Query getOntologyQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}