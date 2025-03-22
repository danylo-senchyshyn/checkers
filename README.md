Hra

Pred vami je moja hra „Dáma“, v ktorej môžu hrať dvaja hráči striedavo.

Pri prvom spustení hry sa zobrazí uvítanie a žiadosť o mená hráčov. Po skončení hry a opätovnom spustení sa už uvítanie a žiadosť o mená nezobrazia, čo je implementované pomocou príznaku, ktorý kontroluje, či sú mená hráčov nastavené.

Po zadaní mien hráčov sa zobrazí herná doska. Na vrchu sa zobrazí stav hry, nižšie sú body každého hráča. Oranžový štvorec je prázdne pole, čierne a biele kruhy sú figúrky a biele a čierne koruny sú dámy. Ďalej sa zobrazuje, kto má právo na ťah — hráč bielych alebo čiernych. Potom sa zobrazí výzva na ťah, v zátvorkách príklad správneho ťahu alebo možnosť ukončiť hru (remíza alebo vzdať sa). V prípade nesprávneho zadania ťahu sa zobrazí varovanie a hráčovi sa opäť ponúkne možnosť zadať ťah. Pri remíze dostanú obaja hráči po 10 bodov. Ak sa hráč vzdá, jeho súper získa 30 bodov, ako pri bežnom víťazstve.

O bodovaní: za zisk figúrky hráč dostane 3 body, za premenenie figúrky na dámu — 5 bodov.

Hlavné pravidlá hry: ak figúrka môže zobrať figúrku súpera, musí tak urobiť. Ak sa hráč pokúsi vykonať neplatný ťah, zobrazí sa varovanie a hráč musí vykonať nový ťah. Figúrka sa pohybuje v jednom smere v závislosti od farby, ale môže zobrať figúrku súpera v oboch smeroch — dopredu aj dozadu. Dáma sa pohybuje po diagonálach vo všetkých smeroch. Keď na doske nezostanú figúrky súpera, hráč vyhráva.

Po skončení hry sa body hráčov uložia do databázy spolu s menami hráčov a časom zápisu.

Po skončení hry sa zobrazí animácia a ponuka dostupných príkazov. Tu si môžete pozrieť: rebríček hráčov, priemerné hodnotenie hry a všetky zanechané komentáre. Všetky tieto informácie sú zobrazené v pekných tabuľkách. Hráči môžu zanechať komentáre a hodnotenia, ale len raz. Pre túto možnosť treba zvoliť príkaz „ac“ (add comment) alebo „ar“ (add rating), po ktorom sa zobrazí výzva na výber farby hráča, ktorý chce zanechať komentár alebo hodnotenie. Hráč môže tiež resetovať hodnotenie, komentáre alebo hodnotenia, ak zadá heslo, čo umožňuje vstup do administrátorského režimu. Tieto príkazy sú zvýraznené modrou farbou. V ponuke je tiež možnosť začať novú hru, pričom sa vymažú všetky body a možnosť zanechať komentár alebo hodnotenie. Pre opustenie ponuky stačí stlačiť „ex“ (exit), čím sa proces ukončí.

Testy
Projekt obsahuje testy na samotnú hru aj na služby. Všetky testy prebehnú úspešne.

[Video](https://youtu.be/eJYQWYea7iI)