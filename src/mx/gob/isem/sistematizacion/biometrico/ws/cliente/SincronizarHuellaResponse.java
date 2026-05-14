
package mx.gob.isem.sistematizacion.biometrico.ws.cliente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="procesada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "procesada"
})
@XmlRootElement(name = "SincronizarHuellaResponse")
public class SincronizarHuellaResponse {

    protected boolean procesada;

    /**
     * Obtiene el valor de la propiedad procesada.
     * 
     */
    public boolean isProcesada() {
        return procesada;
    }

    /**
     * Define el valor de la propiedad procesada.
     * 
     */
    public void setProcesada(boolean value) {
        this.procesada = value;
    }

}
