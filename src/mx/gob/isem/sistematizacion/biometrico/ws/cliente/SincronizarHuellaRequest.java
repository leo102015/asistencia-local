
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
 *         &lt;element name="huella" type="{http://ws.biometrico.central.isem.gob.mx/}Huella"/>
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
    "huella"
})
@XmlRootElement(name = "SincronizarHuellaRequest")
public class SincronizarHuellaRequest {

    @XmlElement(required = true)
    protected Huella huella;

    /**
     * Obtiene el valor de la propiedad huella.
     * 
     * @return
     *     possible object is
     *     {@link Huella }
     *     
     */
    public Huella getHuella() {
        return huella;
    }

    /**
     * Define el valor de la propiedad huella.
     * 
     * @param value
     *     allowed object is
     *     {@link Huella }
     *     
     */
    public void setHuella(Huella value) {
        this.huella = value;
    }

}
