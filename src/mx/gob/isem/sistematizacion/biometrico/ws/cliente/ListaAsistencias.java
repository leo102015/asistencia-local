
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ListaAsistencias complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ListaAsistencias">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="asistencia" type="{http://ws.biometrico.central.isem.gob.mx/}Asistencia" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListaAsistencias", propOrder = {
    "asistencia"
})
public class ListaAsistencias {

    @XmlElement(required = true)
    protected List<Asistencia> asistencia;

    /**
     * Gets the value of the asistencia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the asistencia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAsistencia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Asistencia }
     * 
     * 
     */
    public List<Asistencia> getAsistencia() {
        if (asistencia == null) {
            asistencia = new ArrayList<Asistencia>();
        }
        return this.asistencia;
    }

}
