package huntpage

import com.github.mustachejava.DefaultMustacheFactory
import model.Flat
import java.io.StringWriter
import java.text.NumberFormat
import java.util.Locale

fun buildHuntPage(flats: List<Flat>): String {
    val writer = StringWriter()
    template.execute(writer, flats.toEmailMap())
    return writer.toString()
}

private val template =
    DefaultMustacheFactory()
        .compile("<!doctype html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"utf-8\">\n    <title>Today's Hunt</title>\n    <style type=\"text/css\">\n        body {\n            display: flex;\n            flex-direction: column;\n            align-items: center;\n        }\n\n        .flat {\n            width: 80%;\n            padding: 1% 5%;\n            border: 1px solid black;\n            border-radius: 10px;\n        }\n\n        .flat + .flat {\n            margin-top: 10px;\n        }\n\n        .flat td {\n            width: 50%;\n        }\n\n        tbody th {\n            text-align: left;\n        }\n        tbody td {\n            text-align: left;\n        }\n    </style>\n</head>\n<body>\n{{#flats}}\n    <div class=\"flat\">\n        <table>\n            <thead>\n            <tr>\n                <th colspan=\"2\">\n                    <a href=\"{{url}}\" target=\"_blank\">{{title}}</a>\n                </th>\n            </tr>\n            </thead>\n            <tbody>\n            <tr>\n                <th>\n                    <p>Rooms</p>\n                </th>\n                <td>\n                    <p>{{rooms}}</p>\n                </td>\n            </tr>\n            <tr>\n                <th>\n                    <p>Area</p>\n                </th>\n                <td>\n                    <p>{{area}}</p>\n                </td>\n            </tr>\n            <tr>\n                <th>\n                    <p>Cold Rent</p>\n                </th>\n                <td>\n                    <p>{{coldRent}}</p>\n                </td>\n            </tr>\n            <tr>\n                <th>\n                    <p>Warm Rent</p>\n                </th>\n                <td>\n                    <p>{{warmRent}}</p>\n                </td>\n            </tr>\n            <tr>\n                <th>\n                    <p>Available</p>\n                </th>\n                <td>\n                    <p>{{available}}</p>\n                </td>\n            </tr>\n            <tr>\n                <th>\n                    <p>Address</p>\n                </th>\n                <td>\n                    <a target=\"_blank\" href=\"https://www.google.com/maps/search/{{address}}\">{{address}}</a>\n                </td>\n            </tr>\n            </tbody>\n        </table>\n    </div>\n{{/flats}}\n</body>\n</html>\n\n")

private fun List<Flat>.toEmailMap(): Map<String, List<Map<String, String>>> = mapOf(
    "flats" to this.map { it.toEmailMap() }
)

private fun Flat.toEmailMap() = mapOf<String, String>(
    "title" to title,
    "url" to url,
    "rooms" to rooms,
    "area" to area,
    "coldRent" to coldRent.toEuroString(),
    "warmRent" to warmRent.toEuroString(),
    "available" to available.toString(),
    "address" to address
)

internal fun Int.toEuroString() =
    NumberFormat
        .getCurrencyInstance(Locale.GERMANY)
        .format(this)
