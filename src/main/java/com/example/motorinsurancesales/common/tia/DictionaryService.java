package com.example.motorinsurancesales.common.tia.dictionaries;

///
/// no i tera świetne pytanie:
/// Wszystko co związane ze słownikami walimy tutaj,
/// czy raczej każdy moduł będzie miał swoje własne serwisy słownikowe?
///
/// Generalnie to co powinno się tutaj znaleźć, to coś bardzo "infrowego", a ewentualna segregacja
/// słowników już na poziomie biznesowych subdomen.
///
/// tylko czy w takim razie zostawiamy taki pakiet common?
/// może lepiej pakiet infrastructure?? i tutaj wszystkie współdzielone klienty
///
/// jaka alternatywa??
/// moduł gradlowy infrastructure... tylko wtedy musiałyby też powstać moduły domain i application?
/// wolałbym tym razem nie oddzielać modułami application i domain.
/// infrastructure miałoby dla mnie sens w tym momencie, ale TYLKO dla współdzielonej infry np. dictionaries i cases TIA
///
class DictionaryService {
}
