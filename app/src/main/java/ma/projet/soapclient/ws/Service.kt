package ma.projet.soapclient.ws

import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Service {
    companion object {
        const val NAMESPACE = "http://ws.demo.example.com/"
        const val URL = "http://10.0.2.2:8082/services/ws"
    }

    private fun call(method: String, properties: Map<String, Any> = emptyMap()): Any? {
        val request = SoapObject(NAMESPACE, method)
        properties.forEach { (name, value) -> request.addProperty(name, value) }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply { setOutputSoapObject(request) }
        HttpTransportSE(URL, 15000).call(NAMESPACE + method, envelope)
        return envelope.response
    }

    fun getComptes(): List<Compte> {
        val result = call("getComptes") ?: return emptyList()
        return when (result) {
            is SoapObject -> (0 until result.propertyCount).mapNotNull { parseCompte(result.getProperty(it)) }
            else -> emptyList()
        }
    }

    fun createCompte(solde: Double, type: TypeCompte): Boolean =
        parseCompte(call("createCompte", mapOf("solde" to solde, "type" to type.name))) != null

    fun updateCompte(id: Long, solde: Double, type: TypeCompte): Boolean =
        parseCompte(call("updateCompte", mapOf("id" to id, "solde" to solde, "type" to type.name))) != null

    fun deleteCompte(id: Long): Boolean = call("deleteCompte", mapOf("id" to id)).toString().toBoolean()

    private fun parseCompte(value: Any?): Compte? {
        val soap = value as? SoapObject ?: return null
        val rawDate = soap.getPropertySafelyAsString("dateCreation")
        val date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(rawDate.take(10)) ?: Date()
        } catch (_: Exception) { Date() }
        return Compte(
            soap.getPropertySafelyAsString("id").toLongOrNull(),
            soap.getPropertySafelyAsString("solde").toDoubleOrNull() ?: 0.0,
            date,
            TypeCompte.valueOf(soap.getPropertySafelyAsString("type"))
        )
    }
}
