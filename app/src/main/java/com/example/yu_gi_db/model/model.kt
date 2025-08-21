package com.example.yu_gi_db.model
import com.google.gson.annotations.SerializedName // Importa questo se usi Gson e i nomi delle variabili Kotlin differiscono dalle chiavi JSON

//Anteprima (small) della carta
data class SmallPlayingCard(
        val idCard: String?,
        val strCard: String?,
        val strCardThumb: String?
)
//Lista formata dalla singola carta
data class SmallPlayingCardResponse(
        val cards: List<SmallPlayingCard>?
)

//Carta completa
data class LargePlayingCard(
        val id: Long?,
        val name: String?,
        val type: String?,
        val frameType: String?,
        val desc: String?,
        val atk: Int?,
        val def: Int?,
        val level: Int?, // Anche se è un mostro XYZ, il campo 'level' è presente come 'rank' di solito,
        // ma il JSON lo chiama 'level'. Potrebbe essere un 'rank' mascherato da 'level'.
        val race: String?,
        val attribute: String?,

        @SerializedName("card_sets") // Esempio se il nome della variabile Kotlin fosse diverso, es. cardSets
        val cardSets: List<CardSet>?,

        @SerializedName("card_images")
        val cardImages: List<CardImage>?,

        @SerializedName("card_prices")
        val cardPrices: List<CardPrice>?
)

//Set della carta
data class CardSet(
        @SerializedName("set_name")
        val setName: String?,

        @SerializedName("set_code")
        val setCode: String?,

        @SerializedName("set_rarity")
        val setRarity: String?,

        @SerializedName("set_rarity_code")
        val setRarityCode: String?,

        @SerializedName("set_price")
        val setPrice: String? // Prezzo come Stringa, potrebbe essere meglio convertirlo a Double/Float se necessario
)

//Immagine della carta
data class CardImage(
        val id: Long?,

        @SerializedName("image_url")
        val imageUrl: String?,

        @SerializedName("image_url_small")
        val imageUrlSmall: String?,

        @SerializedName("image_url_cropped")
        val imageUrlCropped: String?
)

//Prezzo della carta nei vari store
data class CardPrice(
        @SerializedName("cardmarket_price")
        val cardmarketPrice: String?, // Anche questi prezzi sono stringhe

        @SerializedName("tcgplayer_price")
        val tcgplayerPrice: String?,

        @SerializedName("ebay_price")
        val ebayPrice: String?,

        @SerializedName("amazon_price")
        val amazonPrice: String?,

        @SerializedName("coolstuffinc_price")
        val coolstuffincPrice: String?
)

//Lista formata dalla singola carta
data class LargePlayingCardResponse(
        val cards: List<LargePlayingCard>?
)