# WishList

En simpel ønskeseddel-applikation bygget med Spring Boot og Thymeleaf.

## Teknologier
- Spring Boot 3.5.6
- Java 21
- Thymeleaf
- MySQL
- Maven

## Temaer
Applikationen har to CSS-temaer:
- **Standard**: `main.css` - Blå og lilla farver
- **Halloween**: `halloween.css` - Orange og mørke farver

For at skifte tema, tilføj eller fjern `halloween.css` linket i HTML-filernes `<head>`:
```html
<link th:href="@{/css/halloween.css}" rel="stylesheet">
```

## Struktur
```
src/main/
├── java/com/example/wishlist/
│   ├── controller/     # HTTP controllers
│   ├── model/          # Data modeller
│   ├── repository/     # Database access
│   ├── service/        # Business logic
│   └── utils/          # Hjælpefunktioner
└── resources/
    ├── static/css/     # Stylesheets
    ├── templates/      # Thymeleaf HTML
    └── application.properties
```

## Design & Frontend
Applikationen har gennemgået en komplet frontend redesign med fokus på moderne UI/UX:

### Farver & Styling
- **Primær farve**: #4A90E2 (blå)
- **Accent farve**: #9B59B6 (lilla)
- Gradient knapper og overgange
- Moderne kort-baseret layout (grid system)
- Bløde skygger og animationer

### Funktioner
- **Dual tema support**: Standard (blå/lilla) og Halloween (orange/mørk)
- **Kort-baserede layouts**: Ønskesedler og ønskeelementer vises som interaktive kort
- **Hover effekter**: Diskrete animationer på knapper, kort og links
- **Empty states**: Venlige beskeder når der ingen data er
- **Gradient design**: Knapper og titler med farve-gradienter
- **Ikoner**: Emoji-baserede visuelle elementer (🎁, 🎈, ⭐, 🔒, 🌐)

### Komponenter
- **Header**: Logo navigation med underline hover effekt
- **Footer**: Simpel enkelt-linje footer
- **Kort**: Wishlist cards, wish item cards med hover transformationer
- **Formularer**: Moderne input felter med fokus states
- **Knapper**: Primary gradient, secondary outline, danger states

### CSS Arkitektur
- **CSS Variables**: Nem tema-håndtering via `:root` variabler
- **Ingen redundans**: Sammenslåede selectors og genbrug af styles
- **Grupperede sektioner**: Logisk organiseret efter komponent-type

## Kør projektet
```bash
mvn spring-boot:run
```
