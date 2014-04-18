package edu.arizona.biosemantics.ols;

/**
 * provided by the ontology lookup webservice
 * @author Fengqiong
 *
 */
public interface QueryService extends javax.xml.rpc.Service {
    public java.lang.String getOntologyQueryAddress();

    public edu.arizona.biosemantics.ols.Query getOntologyQuery() throws javax.xml.rpc.ServiceException;

    public edu.arizona.biosemantics.ols.Query getOntologyQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}