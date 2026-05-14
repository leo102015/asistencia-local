
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Huella complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Huella">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idEmpleado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="indice" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="template" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Huella", propOrder = {
    "idEmpleado",
    "indice",
    "template"
})
public class Huella {

    @XmlElement(required = true)
    protected String idEmpleado;
    protected int indice;
    @XmlElement(required = true)
    protected byte[] template;

    /**
     * Obtiene el valor de la propiedad idEmpleado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Define el valor de la propiedad idEmpleado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdEmpleado(String value) {
        this.idEmpleado = value;
    }

    /**
     * Obtiene el valor de la propiedad indice.
     * 
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Define el valor de la propiedad indice.
     * 
     */
    public void setIndice(int value) {
        this.indice = value;
    }

    /**
     * Obtiene el valor de la propiedad template.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getTemplate() {
        return template;
    }

    /**
     * Define el valor de la propiedad template.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setTemplate(byte[] value) {
        this.template = value;
    }

}
