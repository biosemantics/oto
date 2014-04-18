package edu.arizona.sirls.ols;

/**
 * provided by the ontology lookup webservice
 * @author Fengqiong
 *
 */
public interface QueryService extends javax.xml.rpc.Service {
    public java.lang.String getOntologyQueryAddress();

    public edu.arizona.sirls.ols.Query getOntologyQuery() throws javax.xml.rpc.ServiceException;

    public edu.arizona.sirls.ols.Query getOntologyQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}