
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="asistencias" type="{http://ws.biometrico.central.isem.gob.mx/}ListaAsistencias"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cantidad",
    "asistencias"
})
@XmlRootElement(name = "SincronizarAsistenciasRequest")
public class SincronizarAsistenciasRequest {

    protected int cantidad;
    @XmlElement(required = true)
    protected ListaAsistencias asistencias;

    /**
     * Obtiene el valor de la propiedad cantidad.
     * 
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Define el valor de la propiedad cantidad.
     * 
     */
    public void setCantidad(int value) {
        this.cantidad = value;
    }

    /**
     * Obtiene el valor de la propiedad asistencias.
     * 
     * @return
     *     possible object is
     *     {@link ListaAsistencias }
     *     
     */
    public ListaAsistencias getAsistencias() {
        return asistencias;
    }

    /**
     * Define el valor de la propiedad asistencias.
     * 
     * @param value
     *     allowed object is
     *     {@link ListaAsistencias }
     *     
     */
    public void setAsistencias(ListaAsistencias value) {
        this.asistencias = value;
    }

}
