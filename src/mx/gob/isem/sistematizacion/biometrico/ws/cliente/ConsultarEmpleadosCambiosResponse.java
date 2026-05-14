
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
 *         &lt;element name="empleados" type="{http://ws.biometrico.central.isem.gob.mx/}ListaEmpleados"/>
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
    "empleados"
})
@XmlRootElement(name = "ConsultarEmpleadosCambiosResponse")
public class ConsultarEmpleadosCambiosResponse {

    @XmlElement(required = true)
    protected ListaEmpleados empleados;

    /**
     * Obtiene el valor de la propiedad empleados.
     * 
     * @return
     *     possible object is
     *     {@link ListaEmpleados }
     *     
     */
    public ListaEmpleados getEmpleados() {
        return empleados;
    }

    /**
     * Define el valor de la propiedad empleados.
     * 
     * @param value
     *     allowed object is
     *     {@link ListaEmpleados }
     *     
     */
    public void setEmpleados(ListaEmpleados value) {
        this.empleados = value;
    }

}
