
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ListaHuellas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ListaHuellas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="huella" type="{http://ws.biometrico.central.isem.gob.mx/}Huella" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListaHuellas", propOrder = {
    "huella"
})
public class ListaHuellas {

    @XmlElement(required = true)
    protected List<Huella> huella;

    /**
     * Gets the value of the huella property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the huella property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHuella().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Huella }
     * 
     * 
     */
    public List<Huella> getHuella() {
        if (huella == null) {
            huella = new ArrayList<Huella>();
        }
        return this.huella;
    }

}
