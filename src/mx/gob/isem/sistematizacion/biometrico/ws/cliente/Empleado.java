
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Empleado complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Empleado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="primerApellido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="segundoApellido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rfc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rfid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="agregado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="modificado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="eliminado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="huellas" type="{http://ws.biometrico.central.isem.gob.mx/}ListaHuellas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Empleado", propOrder = {
    "id",
    "nombre",
    "primerApellido",
    "segundoApellido",
    "rfc",
    "rfid",
    "agregado",
    "modificado",
    "eliminado",
    "huellas"
})
public class Empleado {

    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    protected String primerApellido;
    @XmlElement(required = true)
    protected String segundoApellido;
    @XmlElement(required = true)
    protected String rfc;
    protected int rfid;
    protected Boolean agregado;
    protected Boolean modificado;
    protected Boolean eliminado;
    protected ListaHuellas huellas;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad primerApellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimerApellido() {
        return primerApellido;
    }

    /**
     * Define el valor de la propiedad primerApellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimerApellido(String value) {
        this.primerApellido = value;
    }

    /**
     * Obtiene el valor de la propiedad segundoApellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * Define el valor de la propiedad segundoApellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSegundoApellido(String value) {
        this.segundoApellido = value;
    }

    /**
     * Obtiene el valor de la propiedad rfc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfc() {
        return rfc;
    }

    /**
     * Define el valor de la propiedad rfc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfc(String value) {
        this.rfc = value;
    }

    /**
     * Obtiene el valor de la propiedad rfid.
     * 
     */
    public int getRfid() {
        return rfid;
    }

    /**
     * Define el valor de la propiedad rfid.
     * 
     */
    public void setRfid(int value) {
        this.rfid = value;
    }

    /**
     * Obtiene el valor de la propiedad agregado.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAgregado() {
        return agregado;
    }

    /**
     * Define el valor de la propiedad agregado.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAgregado(Boolean value) {
        this.agregado = value;
    }

    /**
     * Obtiene el valor de la propiedad modificado.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isModificado() {
        return modificado;
    }

    /**
     * Define el valor de la propiedad modificado.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setModificado(Boolean value) {
        this.modificado = value;
    }

    /**
     * Obtiene el valor de la propiedad eliminado.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEliminado() {
        return eliminado;
    }

    /**
     * Define el valor de la propiedad eliminado.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEliminado(Boolean value) {
        this.eliminado = value;
    }

    /**
     * Obtiene el valor de la propiedad huellas.
     * 
     * @return
     *     possible object is
     *     {@link ListaHuellas }
     *     
     */
    public ListaHuellas getHuellas() {
        return huellas;
    }

    /**
     * Define el valor de la propiedad huellas.
     * 
     * @param value
     *     allowed object is
     *     {@link ListaHuellas }
     *     
     */
    public void setHuellas(ListaHuellas value) {
        this.huellas = value;
    }

}
