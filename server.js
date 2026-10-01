// Atelier Luxury Fashion Web Application Server
// Provides full mobile web access for iPhone (Safari/PWA) and desktop browsers on port 3000

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

const BRANDS = [
  {
    id: "brand-zara",
    name: "Zara",
    tier: "Contemporary High-Street",
    tierCategory: "High-Street",
    rating: 8.6,
    reviewsCount: 18450,
    priceTier: "$$",
    origin: "Spain (Arteixo)",
    foundedYear: 1975,
    flagshipAddress: "Paseo de la Castellana 79, Nuevos Ministerios, 28046 Madrid, Spain",
    websiteUrl: "https://www.zara.com",
    storePhone: "+34 915 55 93 00",
    mapsQuery: "Zara Paseo de la Castellana 79 Madrid Spain",
    signatureVibe: "Rapid runway translation with sleek European tailoring and clean structural shapes.",
    keyStrengths: ["Oversized Double-Breasted Blazers", "High-Rise Pleated Slacks", "Modern Trend Agility"],
    comboRecommendation: {
      title: "The High-Low Sartorial Contrast",
      description: "Anchor a Zara oversized structured wool-blend blazer with liquid drape alabaster trousers, contrasting accessible fast-fashion agility with quiet luxury footwear.",
      topPiece: "Zara Relaxed Double-Breasted Blazer (Noir)",
      bottomPiece: "Pleated Fluid Barathea Trousers (Pure Alabaster)",
      footwearPiece: "Polished Spazzolato Chunky Loafers (Matte Gold Welt)",
      accessoriesPiece: "Brushed Matte Gold Signet Ring & Leather Crossbody Pouch",
      palette: ["#111111", "#F5F5F0", "#D4AF37", "#333333"],
      stylingRule: "Roll blazer cuffs slightly to reveal tailored wrists; avoid loud branding to cultivate effortless high-fashion intrigue."
    }
  },
  {
    id: "brand-tommy",
    name: "Tommy Hilfiger",
    tier: "American Heritage Prep",
    tierCategory: "Heritage & Prep",
    rating: 8.7,
    reviewsCount: 12900,
    priceTier: "$$$",
    origin: "USA (New York)",
    foundedYear: 1985,
    flagshipAddress: "681 5th Ave, Midtown Manhattan, New York, NY 10022, USA",
    websiteUrl: "https://usa.tommy.com",
    storePhone: "+1 (212) 750-0055",
    mapsQuery: "Tommy Hilfiger 681 5th Ave New York NY",
    signatureVibe: "Classic American Cool with bold collegiate color-blocking and nautical tailoring.",
    keyStrengths: ["Heavyweight Oxford Shirts", "Varsity Bombers", "Structured Linen Chinos"],
    comboRecommendation: {
      title: "Modern Ivy League Architecture",
      description: "Reinterpret Tommy Hilfiger's collegiate DNA by pairing a midnight navy relaxed Oxford shirt with rich camel pleated chinos and clean alabaster minimal kicks.",
      topPiece: "Tommy Hilfiger Relaxed Heavyweight Oxford (Midnight Navy)",
      bottomPiece: "Tailored High-Rise Linen Chinos (Rich Camel)",
      footwearPiece: "Minimalist Full-Grain Nappa Low-Tops (Pure Alabaster)",
      accessoriesPiece: "24K Matte Gold Link Watch & Tortoiseshell Frames",
      palette: ["#1B263B", "#C19A6B", "#F5F5F0", "#D4AF37"],
      stylingRule: "Unbutton the top two collar buttons and half-tuck into high-waisted pleated chinos for modern sprezzatura."
    }
  },
  {
    id: "brand-ralph",
    name: "Ralph Lauren",
    tier: "Old Money Sartorial & Purple Label",
    tierCategory: "Heritage & Prep",
    rating: 9.4,
    reviewsCount: 16800,
    priceTier: "$$$$",
    origin: "USA (New York)",
    foundedYear: 1967,
    flagshipAddress: "888 Madison Ave (Rhinelander Mansion), New York, NY 10021, USA",
    websiteUrl: "https://www.ralphlauren.com",
    storePhone: "+1 (212) 434-8000",
    mapsQuery: "Ralph Lauren Rhinelander Mansion 888 Madison Ave New York",
    signatureVibe: "Unapologetic heritage luxury, equestrian grace, and peerless bespoke suiting.",
    keyStrengths: ["Cable-Knit Cashmere Sweaters", "Double-Faced Flannel Suiting", "Penny Loafers"],
    comboRecommendation: {
      title: "The Hamptons Equestrian Quiet Luxury",
      description: "A buttery camel cashmere knit layered under an unconstructed navy blazer, paired with tailored alabaster trousers and burnished leather derby footwear.",
      topPiece: "Ralph Lauren Cashmere Cable-Knit Polo (Rich Camel)",
      bottomPiece: "Super 130s Flannel Pleated Slacks (Noir Onyx)",
      footwearPiece: "Burnished Calfskin Penny Loafers (Gilded Welt)",
      accessoriesPiece: "Silk Pocket Square & Matte Gold Equestrian Buckle Belt",
      palette: ["#C19A6B", "#111111", "#D4AF37", "#F5F5F0"],
      stylingRule: "Embrace tactile knit texture against flannel; let the camel tone ground the sharp monochrome bottom."
    }
  },
  {
    id: "brand-cos",
    name: "COS",
    tier: "Architectural Minimalist",
    tierCategory: "Quiet Luxury",
    rating: 8.9,
    reviewsCount: 8900,
    priceTier: "$$",
    origin: "Sweden / UK (London)",
    foundedYear: 2007,
    flagshipAddress: "222 Regent St, Mayfair, London W1B 5BD, United Kingdom",
    websiteUrl: "https://www.cos.com",
    storePhone: "+44 20 7478 0400",
    mapsQuery: "COS 222 Regent St London W1B 5BD",
    signatureVibe: "Sculptural forms, reinvented wardrobe classics, and uncompromising fabric integrity.",
    keyStrengths: ["Boxy Poplin Overshirts", "Origami Fold Pleat Trousers", "Cashmere Mock Necks"],
    comboRecommendation: {
      title: "Nordic Brutalist Monolith",
      description: "A boxy minimalist poplin overshirt in charcoal slate paired with high-volume pleated wide-leg trousers and architectural square-toe derby shoes.",
      topPiece: "COS Crisp Organic Poplin Boxy Shirt (Charcoal Slate)",
      bottomPiece: "Architectural Wide-Leg Pleated Pants (Noir Onyx)",
      footwearPiece: "Square-Toe Architectural Derby Shoes (Matte Gold Accent)",
      accessoriesPiece: "Minimalist Leather Crossbody Camera Pouch",
      palette: ["#333333", "#111111", "#F5F5F0", "#D4AF37"],
      stylingRule: "Strictly adhere to clean geometry. Keep jewelry limited to one matte gold chain or architectural cuff."
    }
  },
  {
    id: "brand-massimo",
    name: "Massimo Dutti",
    tier: "Elevated European Tailoring",
    tierCategory: "Quiet Luxury",
    rating: 9.0,
    reviewsCount: 11200,
    priceTier: "$$$",
    origin: "Spain (Barcelona)",
    foundedYear: 1985,
    flagshipAddress: "Passeig de Gràcia 96, Eixample, 08008 Barcelona, Spain",
    websiteUrl: "https://www.massimodutti.com",
    storePhone: "+34 934 87 63 60",
    mapsQuery: "Massimo Dutti Passeig de Gracia 96 Barcelona Spain",
    signatureVibe: "Sophisticated Mediterranean elegance, unconstructed soft tailoring, and noble natural fibers.",
    keyStrengths: ["Unstructured Linen-Silk Blazers", "Tonal Knit Polos", "Suede Chelsea Boots"],
    comboRecommendation: {
      title: "Riviera Sartorial Flâneur",
      description: "An unconstructed midnight navy blazer draped casually over an alabaster silk-cotton polo, grounded with rich camel linen slacks and waxy suede Chelsea boots.",
      topPiece: "Massimo Dutti Soft Tailored Blazer (Midnight Navy)",
      bottomPiece: "Irish Linen High-Rise Chinos (Rich Camel)",
      footwearPiece: "Artisanal Waxy Suede Chelsea Boots (Burnished Tan)",
      accessoriesPiece: "Braided Leather Belt & Gold-Rimmed Acetate Sunglasses",
      palette: ["#1B263B", "#C19A6B", "#F5F5F0", "#D4AF37"],
      stylingRule: "Opt for unlined shoulders and rolled trouser hems with a subtle ankle break to highlight suede footwear."
    }
  },
  {
    id: "brand-gucci",
    name: "Gucci",
    tier: "Florentine Runway Maximalist",
    tierCategory: "Luxury Fashion",
    rating: 9.5,
    reviewsCount: 24100,
    priceTier: "$$$$$",
    origin: "Italy (Florence)",
    foundedYear: 1921,
    flagshipAddress: "Via de' Tornabuoni 73/r, 50123 Firenze FI, Italy",
    websiteUrl: "https://www.gucci.com",
    storePhone: "+39 055 264011",
    mapsQuery: "Gucci Via de Tornabuoni 73 Florence Italy",
    signatureVibe: "Exuberant Italian glamour, archival equestrian horsebit motifs, and theatrical tailoring.",
    keyStrengths: ["Embroidered Velvet Smoking Jackets", "Archival Horsebit Loafers", "Silk Jacquard Scarves"],
    comboRecommendation: {
      title: "Neo-Romantic Runway Opulence",
      description: "A deep bordeaux velvet dinner jacket paired with fluid tailored cigarette slacks, gilded sole derby shoes, and an ornate matte gold choker.",
      topPiece: "Gucci Embroidered Evening Tuxedo (Bordeaux Wine)",
      bottomPiece: "Tailored High-Waist Wool Slacks (Noir Onyx)",
      footwearPiece: "Gilded Hardware Horsebit Loafers (Matte Gold Welt)",
      accessoriesPiece: "24K Matte Gold Link Chain & Archival Brooch",
      palette: ["#5B1424", "#111111", "#D4AF37", "#F5F5F0"],
      stylingRule: "Balance velvet’s rich sheen by keeping trousers matte and razor-sharp; let the footwear hardware gleam."
    }
  },
  {
    id: "brand-prada",
    name: "Prada",
    tier: "Intellectual Post-Industrial Luxury",
    tierCategory: "Luxury Fashion",
    rating: 9.8,
    reviewsCount: 28900,
    priceTier: "$$$$$",
    origin: "Italy (Milan)",
    foundedYear: 1913,
    flagshipAddress: "Galleria Vittorio Emanuele II 63/65, 20121 Milano MI, Italy",
    websiteUrl: "https://www.prada.com",
    storePhone: "+39 02 876979",
    mapsQuery: "Prada Galleria Vittorio Emanuele II Milan Italy",
    signatureVibe: "Subversive minimalism, industrial Re-Nylon ingenuity, and stark conceptual beauty.",
    keyStrengths: ["Re-Nylon Overshirts", "Monolith Spazzolato Footwear", "Razor-Sharp Sarto Slacks"],
    comboRecommendation: {
      title: "Subversive Industrial Chic",
      description: "A sharp Re-Nylon cropped jacket styled with high-waisted cigarette wool slacks and iconic chunky lug-sole platform loafers.",
      topPiece: "Prada Structured Re-Nylon Zip Jacket (Noir Onyx)",
      bottomPiece: "Precision-Cut Cigarette Slacks (Noir Onyx)",
      footwearPiece: "Chunky Architectural Spazzolato Loafers",
      accessoriesPiece: "Brushed Enamel Triangle Clip & Gilded Link Cuff",
      palette: ["#111111", "#F5F5F0", "#D4AF37", "#1B263B"],
      stylingRule: "Embrace pure monochrome noir; contrast technical sheen against matte virgin wool for intellectual depth."
    }
  },
  {
    id: "brand-ysl",
    name: "Saint Laurent",
    tier: "Parisian Rock-Chic Couture",
    tierCategory: "Luxury Fashion",
    rating: 9.6,
    reviewsCount: 21500,
    priceTier: "$$$$$",
    origin: "France (Paris)",
    foundedYear: 1961,
    flagshipAddress: "213 Rue Saint-Honoré, 75001 Paris, France",
    websiteUrl: "https://www.ysl.com",
    storePhone: "+33 1 42 61 74 58",
    mapsQuery: "Saint Laurent 213 Rue Saint Honore Paris France",
    signatureVibe: "Sleek nocturnal tailoring, razor-sharp peak lapels, and androgynous rock-and-roll decadence.",
    keyStrengths: ["Le Smoking Tuxedo Jackets", "Silk Lavallière Blouses", "Wyatt Harness Boots"],
    comboRecommendation: {
      title: "Le Smoking Nocturne",
      description: "An ultra-sharp six-button peak lapel tuxedo jacket worn over an open silk crepe tunic, anchored by skin-tight tailored barathea trousers and pointed boots.",
      topPiece: "Saint Laurent Peak-Lapel Grain de Poudre Smoking (Noir)",
      bottomPiece: "Skinny Barathea Tailored Trousers (Noir Onyx)",
      footwearPiece: "Pointed Leather Wyatt Chelsea Boots (Noir)",
      accessoriesPiece: "Silk Crepe Pocket Square & Fine Gold Collar Pin",
      palette: ["#111111", "#D4AF37", "#FAFAFA", "#333333"],
      stylingRule: "Keep the silhouette ultra-lean and elongated. Minimalist jewelry with sharp metallic sheen only."
    }
  },
  {
    id: "brand-bottega",
    name: "Bottega Veneta",
    tier: "Sensual Leather Craft & Quiet Luxury",
    tierCategory: "Quiet Luxury",
    rating: 9.7,
    reviewsCount: 19300,
    priceTier: "$$$$$",
    origin: "Italy (Vicenza)",
    foundedYear: 1966,
    flagshipAddress: "Via Montenapoleone 27/A, 20121 Milano MI, Italy",
    websiteUrl: "https://www.bottegaveneta.com",
    storePhone: "+39 02 7602 4495",
    mapsQuery: "Bottega Veneta Via Montenapoleone 27 Milan Italy",
    signatureVibe: "Tactile supremacy, monumental fluid leather silhouettes, and no visible logos.",
    keyStrengths: ["Intrecciato Woven Leather Goods", "Puddle Wool Trousers", "Tire Combat Boots"],
    comboRecommendation: {
      title: "Monumental Tactile Drapery",
      description: "An oversized olive drab buttery leather overshirt matched with puddle-hem fluid wool slacks and bold architectural platform footwear.",
      topPiece: "Bottega Veneta Sculptural Nappa Overshirt (Olive Drab)",
      bottomPiece: "Puddle Wide-Leg Wool Slacks (Pure Alabaster)",
      footwearPiece: "Architectural Tire Sole Footwear (Matte Gold Accent)",
      accessoriesPiece: "Intrecciato Leather Pouch & Sculpted Gold Ring",
      palette: ["#4B5320", "#F5F5F0", "#D4AF37", "#111111"],
      stylingRule: "Allow the trouser hem to break generously over the chunky boot welt to celebrate fluid volume."
    }
  },
  {
    id: "brand-jacquemus",
    name: "Jacquemus",
    tier: "Provençal Sun-Drenched Avant-Garde",
    tierCategory: "Luxury Fashion",
    rating: 9.3,
    reviewsCount: 15700,
    priceTier: "$$$$",
    origin: "France (Salon-de-Provence)",
    foundedYear: 2009,
    flagshipAddress: "58 Avenue Montaigne, 75008 Paris, France",
    websiteUrl: "https://www.jacquemus.com",
    storePhone: "+33 1 42 68 00 24",
    mapsQuery: "Jacquemus 58 Avenue Montaigne Paris France",
    signatureVibe: "Sun-bleached linen poetry, playful geometric asymmetry, and warm Mediterranean sensuality.",
    keyStrengths: ["La Chemise Asymmetric Tops", "Linen Draped Bermudas", "Micro-Chiquito Bags"],
    comboRecommendation: {
      title: "Provençal Sunburst Solstice",
      description: "An asymmetric drape terracotta linen shirt styled with fluid high-waisted alabaster slacks and minimal leather strappy mules.",
      topPiece: "Jacquemus Draped Asymmetric Linen Shirt (Terracotta Rust)",
      bottomPiece: "Pleated Wide-Leg Trousers (Pure Alabaster)",
      footwearPiece: "Minimalist Architectural Leather Slides (Matte Gold)",
      accessoriesPiece: "Micro Le Chiquito Crossbody & Gilded Sunflower Earring",
      palette: ["#A0522D", "#F5F5F0", "#D4AF37", "#C19A6B"],
      stylingRule: "Tuck one side of the linen shirt loosely while letting the terracotta fabric billow naturally in movement."
    }
  }
];

const MANIFEST = {
  name: "Atelier Haute Couture",
  short_name: "Atelier",
  description: "Haute couture 3D fashion fitting room, lookbook studio, brand directory, and AI stylist.",
  start_url: "/",
  display: "standalone",
  background_color: "#111111",
  theme_color: "#D4AF37",
  icons: [
    {
      src: "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=512&q=80",
      sizes: "512x512",
      type: "image/jpeg"
    }
  ]
};

function getHtml() {
  const brandsJson = JSON.stringify(BRANDS);
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
  <title>Atelier — Haute Couture & Brand Directory</title>
  
  <!-- Apple Web App & PWA Settings for iPhone users -->
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
  <meta name="apple-mobile-web-app-title" content="Atelier">
  <meta name="theme-color" content="#111111">
  <link rel="manifest" href="/manifest.json">
  <link rel="apple-touch-icon" href="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=192&q=80">
  
  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@500;700;800&family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  
  <!-- Three.js for 3D Mannequin -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>

  <style>
    :root {
      --gold: #D4AF37;
      --gold-dark: #AA820A;
      --gold-light: #F3E5AB;
      --bg-dark: #0D0D0E;
      --card-dark: #18181A;
      --card-border: #2C2C30;
      --text-main: #EDEDED;
      --text-muted: #9E9EA7;
    }
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
    }
    body {
      background-color: var(--bg-dark);
      color: var(--text-main);
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
      min-height: 100vh;
      overflow-x: hidden;
      padding-bottom: 90px;
    }
    header {
      position: sticky;
      top: 0;
      z-index: 50;
      background: rgba(13, 13, 14, 0.85);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border-bottom: 1px solid var(--card-border);
      padding: 14px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .brand-title {
      font-family: 'Cinzel', serif;
      font-size: 20px;
      font-weight: 800;
      letter-spacing: 2px;
      color: #fff;
    }
    .brand-sub {
      font-size: 9px;
      letter-spacing: 1.5px;
      color: var(--gold);
      text-transform: uppercase;
      font-weight: 600;
    }
    .edition-tag {
      background: rgba(212, 175, 55, 0.12);
      border: 1px solid var(--gold);
      color: var(--gold);
      font-size: 10px;
      padding: 4px 10px;
      border-radius: 9999px;
      font-weight: 600;
      letter-spacing: 1px;
    }
    .container {
      max-width: 900px;
      margin: 0 auto;
      padding: 16px 20px;
    }
    .nav-tabs {
      display: flex;
      gap: 8px;
      overflow-x: auto;
      padding-bottom: 4px;
      margin-bottom: 18px;
      scrollbar-width: none;
    }
    .nav-tabs::-webkit-scrollbar { display: none; }
    .nav-tab {
      background: var(--card-dark);
      border: 1px solid var(--card-border);
      color: var(--text-muted);
      padding: 10px 18px;
      border-radius: 9999px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s ease;
    }
    .nav-tab.active {
      background: var(--gold);
      color: #000;
      border-color: var(--gold);
      box-shadow: 0 4px 14px rgba(212, 175, 55, 0.3);
    }

    /* 3D Canvas Box */
    .studio-box {
      position: relative;
      width: 100%;
      height: 380px;
      background: radial-gradient(circle at center, #26262B 0%, #121214 100%);
      border-radius: 18px;
      border: 1px solid var(--card-border);
      overflow: hidden;
      margin-bottom: 24px;
    }
    #mannequin-canvas {
      width: 100% !important;
      height: 100% !important;
      display: block;
    }
    .studio-badge {
      position: absolute;
      top: 14px;
      left: 14px;
      background: rgba(0, 0, 0, 0.65);
      border: 1px solid var(--gold);
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 10px;
      font-weight: 700;
      color: var(--gold);
      letter-spacing: 1px;
    }
    .studio-controls {
      position: absolute;
      bottom: 14px;
      left: 14px;
      right: 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: auto;
    }
    .studio-btn-row {
      display: flex;
      justify-content: space-between;
      gap: 8px;
    }
    .studio-btn {
      flex: 1;
      background: rgba(18, 18, 20, 0.85);
      border: 1px solid var(--card-border);
      color: #fff;
      padding: 8px 12px;
      border-radius: 8px;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
      backdrop-filter: blur(8px);
      transition: all 0.2s;
      text-align: center;
    }
    .studio-btn:hover { border-color: var(--gold); color: var(--gold); }
    .skin-selector-bar {
      background: rgba(18, 18, 20, 0.90);
      border: 1px solid rgba(212, 175, 55, 0.4);
      border-radius: 10px;
      padding: 8px 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 8px;
      backdrop-filter: blur(8px);
    }
    .skin-label-text {
      font-size: 10px;
      font-weight: 700;
      color: var(--gold);
      letter-spacing: 0.8px;
      white-space: nowrap;
    }
    .skin-swatches-strip {
      display: flex;
      gap: 6px;
      align-items: center;
      overflow-x: auto;
    }
    .skin-swatch-item {
      width: 22px;
      height: 22px;
      border-radius: 50%;
      cursor: pointer;
      border: 1.5px solid #444;
      transition: transform 0.15s, border-color 0.15s;
      flex-shrink: 0;
    }
    .skin-swatch-item:hover {
      transform: scale(1.15);
      border-color: #fff;
    }
    .skin-swatch-item.active {
      border-color: var(--gold);
      box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.5);
      transform: scale(1.18);
    }

    /* Search & Filter Bar */
    .search-card {
      background: var(--card-dark);
      border: 1px solid var(--card-border);
      border-radius: 16px;
      padding: 16px;
      margin-bottom: 20px;
    }
    .search-input-wrap {
      position: relative;
      margin-bottom: 12px;
    }
    .search-input {
      width: 100%;
      background: #0E0E10;
      border: 1px solid #333338;
      border-radius: 12px;
      padding: 12px 16px;
      color: #fff;
      font-size: 14px;
      outline: none;
      transition: border-color 0.2s;
    }
    .search-input:focus {
      border-color: var(--gold);
    }
    .filter-chips {
      display: flex;
      gap: 6px;
      overflow-x: auto;
      scrollbar-width: none;
    }
    .filter-chips::-webkit-scrollbar { display: none; }
    .chip {
      background: #222226;
      border: 1px solid #383840;
      color: var(--text-muted);
      font-size: 11px;
      padding: 6px 12px;
      border-radius: 9999px;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s;
    }
    .chip.active {
      background: rgba(212, 175, 55, 0.2);
      border-color: var(--gold);
      color: var(--gold);
      font-weight: 700;
    }

    /* Brand Card */
    .brand-list {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    .brand-card {
      background: var(--card-dark);
      border: 1px solid var(--card-border);
      border-radius: 18px;
      padding: 20px;
      transition: transform 0.2s, border-color 0.2s;
    }
    .brand-card:hover {
      border-color: rgba(212, 175, 55, 0.4);
    }
    .brand-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;
    }
    .brand-id-block {
      display: flex;
      gap: 14px;
      align-items: center;
    }
    .brand-avatar {
      width: 46px;
      height: 46px;
      border-radius: 50%;
      background: #25252A;
      border: 1.5px solid var(--gold);
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: 'Cinzel', serif;
      font-size: 18px;
      font-weight: 700;
      color: var(--gold);
    }
    .brand-name {
      font-size: 18px;
      font-weight: 700;
      color: #fff;
    }
    .brand-meta {
      font-size: 12px;
      color: var(--text-muted);
      margin-top: 2px;
    }
    .rating-badge {
      background: var(--gold);
      color: #000;
      font-size: 11px;
      font-weight: 700;
      padding: 4px 8px;
      border-radius: 8px;
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
    .price-badge {
      font-size: 11px;
      font-weight: 700;
      color: var(--gold);
      background: #202024;
      border: 1px solid #333338;
      padding: 2px 8px;
      border-radius: 6px;
      margin-top: 4px;
      text-align: right;
    }
    .tier-badge {
      display: inline-block;
      background: rgba(212, 175, 55, 0.1);
      border: 1px solid rgba(212, 175, 55, 0.3);
      color: var(--gold);
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 0.8px;
      text-transform: uppercase;
      padding: 4px 10px;
      border-radius: 6px;
      margin-bottom: 10px;
    }
    .brand-vibe {
      font-size: 13px;
      line-height: 1.5;
      color: #D1D1D6;
      margin-bottom: 14px;
    }
    .strength-chips {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;
    }
    .strength-chip {
      background: #222226;
      border: 1px solid #333338;
      color: var(--text-muted);
      font-size: 11px;
      padding: 4px 10px;
      border-radius: 8px;
    }

    /* Address & Locator Box */
    .address-box {
      background: #101012;
      border: 1px solid rgba(212, 175, 55, 0.4);
      border-radius: 12px;
      padding: 14px;
      margin-bottom: 16px;
    }
    .address-title {
      font-size: 11px;
      font-weight: 700;
      color: var(--gold);
      letter-spacing: 1px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }
    .address-val {
      font-size: 13px;
      color: #fff;
      font-weight: 500;
      margin-bottom: 6px;
      line-height: 1.4;
    }
    .phone-val {
      font-size: 12px;
      color: var(--text-muted);
      margin-bottom: 12px;
    }
    .address-actions {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
    }
    .btn-action {
      background: #222226;
      border: 1px solid #383840;
      color: #fff;
      font-size: 11px;
      font-weight: 600;
      padding: 8px 12px;
      border-radius: 8px;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      gap: 5px;
      cursor: pointer;
      transition: all 0.2s;
    }
    .btn-action.primary {
      background: var(--gold);
      color: #000;
      border-color: var(--gold);
      font-weight: 700;
    }
    .btn-action:hover {
      opacity: 0.9;
      transform: translateY(-1px);
    }

    /* Combo Box */
    .combo-box {
      background: #131316;
      border: 1px solid rgba(212, 175, 55, 0.5);
      border-radius: 14px;
      padding: 16px;
    }
    .combo-header {
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 1px;
      color: var(--gold);
      text-transform: uppercase;
      margin-bottom: 6px;
      display: flex;
      align-items: center;
      gap: 5px;
    }
    .combo-title {
      font-size: 15px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 6px;
    }
    .combo-desc {
      font-size: 12px;
      color: var(--text-muted);
      line-height: 1.5;
      margin-bottom: 12px;
    }
    .pieces-list {
      background: #1A1A1E;
      border-radius: 8px;
      padding: 10px 12px;
      font-size: 12px;
      margin-bottom: 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .palette-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
    }
    .palette-swatch {
      width: 20px;
      height: 20px;
      border-radius: 50%;
      border: 1px solid var(--gold);
    }
    .styling-rule {
      background: rgba(0, 0, 0, 0.4);
      border-left: 2px solid var(--gold);
      padding: 8px 12px;
      font-size: 11px;
      font-family: serif;
      font-style: italic;
      color: #D1D1D6;
      margin-bottom: 14px;
    }
    .combo-actions {
      display: flex;
      gap: 10px;
    }
    .combo-btn {
      flex: 1;
      background: var(--gold);
      color: #000;
      border: none;
      padding: 10px;
      border-radius: 8px;
      font-size: 12px;
      font-weight: 700;
      cursor: pointer;
      text-align: center;
      transition: background-color 0.2s;
    }
    .combo-btn.secondary {
      background: transparent;
      border: 1px solid #44444A;
      color: #fff;
    }
    .combo-btn:hover { opacity: 0.9; }

    /* Toast Notification */
    #toast {
      position: fixed;
      bottom: 24px;
      left: 50%;
      transform: translateX(-50%) translateY(100px);
      background: #1F1F24;
      border: 1px solid var(--gold);
      color: #fff;
      padding: 12px 24px;
      border-radius: 9999px;
      font-size: 13px;
      font-weight: 600;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.6);
      transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
      z-index: 100;
      pointer-events: none;
    }
    #toast.show {
      transform: translateX(-50%) translateY(0);
    }
  </style>
</head>
<body>

  <header>
    <div>
      <div class="brand-title">ATELIER</div>
      <div class="brand-sub">Sartorial Lookbook & 3D Fitting</div>
    </div>
    <div class="edition-tag">ONLINE EDITION</div>
  </header>

  <div class="container">

    <!-- Navigation Tabs -->
    <div class="nav-tabs">
      <div class="nav-tab active" onclick="switchTab('brands')">Luxury Brand Directory</div>
      <div class="nav-tab" onclick="switchTab('studio')">3D Fitting Studio</div>
      <div class="nav-tab" onclick="switchTab('vault')">Fit Vault</div>
      <div class="nav-tab" onclick="switchTab('stylist')">AI Stylist</div>
    </div>

    <!-- 3D STUDIO SECTION (Always Interactive) -->
    <div id="section-studio" class="studio-box">
      <div class="studio-badge">3D BESPOKE MANNEQUIN</div>
      <div id="canvas-container" style="width: 100%; height: 100%;"></div>
      <div class="studio-controls">
        <div class="skin-selector-bar">
          <div class="skin-label-text">
            <span>COMPLEXION:</span>
            <span id="skin-label-name" style="color: #fff; margin-left: 4px;">Warm Ivory</span>
          </div>
          <div class="skin-swatches-strip" id="skin-swatches-container"></div>
        </div>
        <div class="studio-btn-row">
          <button class="studio-btn" onclick="toggleGender()">Gender (<span id="gender-label">Male</span>)</button>
          <button class="studio-btn" onclick="cycleBody()">Silhouette: <span id="body-label">Slim</span></button>
          <button class="studio-btn" onclick="rotateStudio()">Rotate 360°</button>
        </div>
      </div>
    </div>

    <!-- BRAND SEARCH SPACE -->
    <div id="section-brands">
      <div class="search-card">
        <div class="search-input-wrap">
          <input 
            type="text" 
            id="brand-search" 
            class="search-input" 
            placeholder="Search Zara, Tommy Hilfiger, Prada, Paris, Madrid, London..." 
            oninput="handleSearch(this.value)"
          />
        </div>
        <div class="filter-chips">
          <div class="chip active" onclick="setTierFilter('All', this)">All Tiers</div>
          <div class="chip" onclick="setTierFilter('High-Street', this)">High-Street</div>
          <div class="chip" onclick="setTierFilter('Heritage & Prep', this)">Heritage & Prep</div>
          <div class="chip" onclick="setTierFilter('Quiet Luxury', this)">Quiet Luxury</div>
          <div class="chip" onclick="setTierFilter('Luxury Fashion', this)">Haute Couture</div>
        </div>
      </div>

      <!-- Brand Directory Cards Container -->
      <div id="brands-container" class="brand-list"></div>
    </div>

    <!-- FIT VAULT SECTION -->
    <div id="section-vault" style="display: none;">
      <div class="search-card">
        <h3 style="color: var(--gold); font-size: 16px; margin-bottom: 8px;">ARCHIVED FIT VAULT</h3>
        <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 16px;">Saved luxury brand combos and bespoke styling formulas.</p>
        <div id="vault-list" style="display: flex; flex-direction: column; gap: 12px;"></div>
      </div>
    </div>

    <!-- AI STYLIST SECTION -->
    <div id="section-stylist" style="display: none;">
      <div class="search-card">
        <h3 style="color: var(--gold); font-size: 16px; margin-bottom: 8px;">ATELIER AI STYLIST CONCIERGE</h3>
        <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 16px;">Editorial recommendations tailored to luxury silhouettes.</p>
        <div style="background: #101012; border-radius: 12px; padding: 14px; margin-bottom: 14px; border-left: 3px solid var(--gold);">
          <div style="font-weight: 700; font-size: 12px; color: var(--gold); margin-bottom: 4px;">Atelier Stylist:</div>
          <div style="font-size: 13px; color: #E0E0E0; line-height: 1.5;">
            "Welcome to the Atelier salon. Looking for an outfit formula for Tommy Hilfiger relaxed ivy style, a Zara sharp high-low contrast, or quiet luxury from Bottega Veneta and COS? Tap 'Try in 3D' on any brand combo to visualize the color harmony in real-time."
          </div>
        </div>
      </div>
    </div>

  </div>

  <div id="toast">Address copied to clipboard!</div>

  <script>
    const brandsData = ${brandsJson};
    let currentFilter = 'All';
    let searchQuery = '';
    let savedVault = [
      {
        brand: "Tommy Hilfiger",
        title: "Modern Ivy League Architecture",
        pieces: "Relaxed Heavyweight Oxford (Midnight Navy) • Chinos (Camel) • Minimalist Kicks (Alabaster)",
        palette: ["#1B263B", "#C19A6B", "#F5F5F0", "#D4AF37"]
      },
      {
        brand: "Zara",
        title: "The High-Low Sartorial Contrast",
        pieces: "Relaxed Double-Breasted Blazer (Noir) • Barathea Trousers (Alabaster) • Chunky Loafers",
        palette: ["#111111", "#F5F5F0", "#D4AF37", "#333333"]
      }
    ];

    function renderBrands() {
      const container = document.getElementById('brands-container');
      const filtered = brandsData.filter(b => {
        const matchesTier = currentFilter === 'All' || b.tierCategory.toLowerCase() === currentFilter.toLowerCase();
        const q = searchQuery.toLowerCase();
        const matchesQuery = !q || 
          b.name.toLowerCase().includes(q) ||
          b.tier.toLowerCase().includes(q) ||
          b.origin.toLowerCase().includes(q) ||
          b.flagshipAddress.toLowerCase().includes(q) ||
          b.signatureVibe.toLowerCase().includes(q) ||
          b.comboRecommendation.title.toLowerCase().includes(q);
        return matchesTier && matchesQuery;
      });

      if (filtered.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--text-muted);">No luxury brands found matching your search.</div>';
        return;
      }

      container.innerHTML = filtered.map(b => {
        const c = b.comboRecommendation;
        const encodedMaps = encodeURIComponent(b.mapsQuery);
        return \`
          <div class="brand-card">
            <div class="brand-header">
              <div class="brand-id-block">
                <div class="brand-avatar">\${b.name.charAt(0)}</div>
                <div>
                  <div class="brand-name">\${b.name}</div>
                  <div class="brand-meta">\${b.origin} • Est. \${b.foundedYear}</div>
                </div>
              </div>
              <div>
                <div class="rating-badge">★ \${b.rating} / 10</div>
                <div class="price-badge">\${b.priceTier}</div>
              </div>
            </div>

            <div class="tier-badge">\${b.tier}</div>
            <div class="brand-vibe">\${b.signatureVibe}</div>

            <div class="strength-chips">
              \${b.keyStrengths.map(s => \`<span class="strength-chip">\${s}</span>\`).join('')}
            </div>

            <!-- FLAGSHIP BOUTIQUE ADDRESS & STORE LOCATOR -->
            <div class="address-box">
              <div class="address-title">
                <span>📍 FLAGSHIP BOUTIQUE ADDRESS</span>
                <span style="font-size: 9px; opacity: 0.8; border: 1px solid var(--gold); padding: 2px 6px; border-radius: 4px;">VERIFIED STORE</span>
              </div>
              <div class="address-val">\${b.flagshipAddress}</div>
              <div class="phone-val">Concierge: \${b.storePhone}</div>
              <div class="address-actions">
                <a href="https://www.google.com/maps/search/?api=1&query=\${encodedMaps}" target="_blank" rel="noopener noreferrer" class="btn-action primary">
                  🧭 Get Directions (Maps)
                </a>
                <a href="\${b.websiteUrl}" target="_blank" rel="noopener noreferrer" class="btn-action">
                  🌐 Visit Store Online
                </a>
                <a href="tel:\${b.storePhone.replace(/\\s+/g, '')}" class="btn-action">
                  📞 Call Concierge
                </a>
                <button onclick="copyAddress('\${b.name}', '\${b.flagshipAddress}')" class="btn-action">
                  📋 Copy Address
                </button>
              </div>
            </div>

            <!-- SUITABLE FASHION COMBO RECOMMENDATION -->
            <div class="combo-box">
              <div class="combo-header">✨ RECOMMENDED FASHION COMBO</div>
              <div class="combo-title">\${c.title}</div>
              <div class="combo-desc">\${c.description}</div>

              <div class="pieces-list">
                <div><strong>Top:</strong> \${c.topPiece}</div>
                <div><strong>Bottom:</strong> \${c.bottomPiece}</div>
                <div><strong>Footwear:</strong> \${c.footwearPiece}</div>
                <div><strong>Accents:</strong> \${c.accessoriesPiece}</div>
              </div>

              <div class="palette-row">
                <span style="font-size: 11px; font-weight: 700; color: var(--gold);">Combo Palette:</span>
                \${c.palette.map(hex => \`<div class="palette-swatch" style="background-color: \${hex};"></div>\`).join('')}
              </div>

              <div class="styling-rule">“\${c.stylingRule}”</div>

              <div class="combo-actions">
                <button class="combo-btn" onclick="applyComboToStudio('\${c.palette[0]}', '\${c.palette[1]}', '\${c.palette[2]}')">
                  Try in 3D Studio
                </button>
                <button class="combo-btn secondary" onclick="saveToVault('\${b.name}', '\${c.title}', '\${c.topPiece} • \${c.bottomPiece}')">
                  Save to Vault
                </button>
              </div>
            </div>
          </div>
        \`;
      }).join('');
    }

    function handleSearch(val) {
      searchQuery = val;
      renderBrands();
    }

    function setTierFilter(tier, el) {
      currentFilter = tier;
      document.querySelectorAll('.filter-chips .chip').forEach(c => c.classList.remove('active'));
      el.classList.add('active');
      renderBrands();
    }

    function copyAddress(name, address) {
      navigator.clipboard.writeText(name + " Flagship: " + address).then(() => {
        showToast(name + " address copied to clipboard!");
      }).catch(() => {
        showToast("Address copied!");
      });
    }

    function showToast(msg) {
      const t = document.getElementById('toast');
      t.textContent = msg;
      t.classList.add('show');
      setTimeout(() => { t.classList.remove('show'); }, 2800);
    }

    function switchTab(tab) {
      document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
      event.target.classList.add('active');

      document.getElementById('section-brands').style.display = (tab === 'brands') ? 'block' : 'none';
      document.getElementById('section-vault').style.display = (tab === 'vault') ? 'block' : 'none';
      document.getElementById('section-stylist').style.display = (tab === 'stylist') ? 'block' : 'none';

      if (tab === 'vault') renderVault();
    }

    function renderVault() {
      const container = document.getElementById('vault-list');
      container.innerHTML = savedVault.map(v => \`
        <div style="background: #141418; border: 1px solid var(--card-border); border-radius: 12px; padding: 12px 16px;">
          <div style="font-weight: 700; color: var(--gold); font-size: 13px;">\${v.brand} • \${v.title}</div>
          <div style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">\${v.pieces}</div>
        </div>
      \`).join('');
    }

    function saveToVault(brand, title, pieces) {
      savedVault.unshift({ brand, title, pieces });
      showToast("Saved " + brand + " combo to Fit Vault!");
    }

    // ==========================================
    // THREE.JS 3D FITTING ROOM
    // ==========================================
    let scene, camera, renderer, mannequinGroup, currentGender = 'male', currentBody = 'Slim';
    let topMesh, bottomMesh, shoesMesh;
    let currentSkinTone = '#EEDBC8';
    let currentTopColor = '#111111', currentBottomColor = '#18181C', currentShoesColor = '#D4AF37';

    const skinTonePresets = [
      { id: 'alabaster', name: 'Alabaster', hex: '#F7EBE1' },
      { id: 'ivory', name: 'Warm Ivory', hex: '#EEDBC8' },
      { id: 'bisque', name: 'Golden Bisque', hex: '#E2C7A8' },
      { id: 'olive', name: 'Honey Olive', hex: '#CBA17B' },
      { id: 'caramel', name: 'Warm Caramel', hex: '#A8754D' },
      { id: 'chestnut', name: 'Spiced Chestnut', hex: '#865434' },
      { id: 'espresso', name: 'Deep Espresso', hex: '#5C3621' },
      { id: 'ebony', name: 'Midnight Obsidian', hex: '#382018' }
    ];

    function init3DStudio() {
      const container = document.getElementById('canvas-container');
      const width = container.clientWidth || 600;
      const height = container.clientHeight || 380;

      scene = new THREE.Scene();
      camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 100);
      camera.position.set(0, 1.2, 3.8);

      renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
      renderer.setSize(width, height);
      renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      container.appendChild(renderer.domElement);

      const ambLight = new THREE.AmbientLight(0xffffff, 0.7);
      scene.add(ambLight);

      const dirLight = new THREE.DirectionalLight(0xD4AF37, 1.2);
      dirLight.position.set(3, 5, 4);
      scene.add(dirLight);

      mannequinGroup = new THREE.Group();
      scene.add(mannequinGroup);

      buildMannequin('#111111', '#F5F5F0', '#D4AF37');

      // Drag to rotate
      let isDragging = false, prevX = 0;
      renderer.domElement.addEventListener('pointerdown', (e) => { isDragging = true; prevX = e.clientX; });
      window.addEventListener('pointermove', (e) => {
        if (!isDragging) return;
        const delta = e.clientX - prevX;
        mannequinGroup.rotation.y += delta * 0.015;
        prevX = e.clientX;
      });
      window.addEventListener('pointerup', () => { isDragging = false; });

      function animate() {
        requestAnimationFrame(animate);
        renderer.render(scene, camera);
      }
      animate();

      window.addEventListener('resize', () => {
        const w = container.clientWidth;
        const h = container.clientHeight;
        camera.aspect = w / h;
        camera.updateProjectionMatrix();
        renderer.setSize(w, h);
      });
    }

    function buildMannequin(topColor, bottomColor, shoesColor, skinColor) {
      if (topColor) currentTopColor = topColor;
      if (bottomColor) currentBottomColor = bottomColor;
      if (shoesColor) currentShoesColor = shoesColor;
      if (skinColor) currentSkinTone = skinColor;

      while(mannequinGroup.children.length > 0) {
        mannequinGroup.remove(mannequinGroup.children[0]);
      }

      const isFemale = currentGender === 'female';
      const wMult = currentBody === 'Slim' ? 0.90 : currentBody === 'Athletic' ? 1.05 : currentBody === 'Broad' ? 1.16 : 1.25;

      const bodyMat = new THREE.MeshStandardMaterial({
        color: new THREE.Color(currentSkinTone || '#EEDBC8'),
        roughness: 0.38,
        metalness: 0.02
      });
      const boxerMat = new THREE.MeshStandardMaterial({ color: currentBottomColor || '#18181C', roughness: 0.55, metalness: 0.04 });
      const waistbandMat = new THREE.MeshStandardMaterial({ color: 0xD4AF37, roughness: 0.35, metalness: 0.40 });
      const accentMat = new THREE.MeshStandardMaterial({ color: currentTopColor || '#D4AF37', roughness: 0.28, metalness: 0.65 });
      const goldMat = new THREE.MeshStandardMaterial({ color: 0xD4AF37, roughness: 0.22, metalness: 0.88 });
      const darkMat = new THREE.MeshStandardMaterial({ color: 0x1A1A1E, roughness: 0.32, metalness: 0.15 });

      // Atelier Chrome/Gold Support Spine
      const standPole = new THREE.Mesh(new THREE.CylinderGeometry(0.012, 0.012, 1.20, 24), goldMat);
      standPole.position.set(0, 0.60, -0.16);
      mannequinGroup.add(standPole);

      // Soft diffused contact shadows beneath bare feet
      [-0.125 * wMult, 0.125 * wMult].forEach(fX => {
        const shadowGeo = new THREE.CircleGeometry(0.12 * wMult, 24);
        shadowGeo.rotateX(-Math.PI / 2);
        const shadowMat = new THREE.MeshBasicMaterial({ color: 0x000000, transparent: true, opacity: 0.45 });
        const shadowMesh = new THREE.Mesh(shadowGeo, shadowMat);
        shadowMesh.position.set(fX, 0.005, 0.04);
        mannequinGroup.add(shadowMesh);
      });

      // 1. SCULPTED HIGH-FASHION HEAD & JAW (SMOOTH, NO BLOCKS)
      const headGroup = new THREE.Group();
      headGroup.position.set(0, 1.82, 0);

      const craniumGeo = new THREE.SphereGeometry(0.115, 32, 32);
      craniumGeo.scale(0.84, 1.15, 0.96);
      headGroup.add(new THREE.Mesh(craniumGeo, bodyMat));

      const chinGeo = new THREE.SphereGeometry(0.046, 24, 24);
      chinGeo.scale(0.85, 0.95, 1.15);
      const chin = new THREE.Mesh(chinGeo, bodyMat);
      chin.position.set(0, -0.075, 0.04);
      headGroup.add(chin);

      [-1, 1].forEach(side => {
        const jawCurve = new THREE.Mesh(new THREE.SphereGeometry(0.038, 20, 20), bodyMat);
        jawCurve.scale.set(0.65, 0.90, 1.2);
        jawCurve.position.set(side * 0.048, -0.05, 0.015);
        jawCurve.rotation.y = side * 0.25;
        headGroup.add(jawCurve);

        const cheek = new THREE.Mesh(new THREE.SphereGeometry(0.028, 18, 18), bodyMat);
        cheek.scale.set(0.55, 1.15, 0.85);
        cheek.position.set(side * 0.066, -0.012, 0.062);
        headGroup.add(cheek);

        const earGeo = new THREE.TorusGeometry(0.018, 0.0055, 12, 24);
        earGeo.scale(0.55, 1.25, 0.85);
        const ear = new THREE.Mesh(earGeo, bodyMat);
        ear.position.set(side * 0.094, -0.005, -0.012);
        ear.rotation.y = side * 0.25;
        headGroup.add(ear);
      });

      const noseBridge = new THREE.Mesh(new THREE.CylinderGeometry(0.005, 0.011, 0.048, 16), bodyMat);
      noseBridge.rotation.x = Math.PI * 0.38;
      noseBridge.position.set(0, -0.008, 0.088);
      headGroup.add(noseBridge);

      const noseTip = new THREE.Mesh(new THREE.SphereGeometry(0.0075, 16, 16), bodyMat);
      noseTip.position.set(0, -0.024, 0.104);
      headGroup.add(noseTip);

      const lips = new THREE.Mesh(new THREE.SphereGeometry(0.012, 16, 12), bodyMat);
      lips.scale.set(1.4, 0.45, 0.6);
      lips.position.set(0, -0.052, 0.078);
      headGroup.add(lips);

      const hairGeo = new THREE.SphereGeometry(0.118, 28, 28);
      hairGeo.scale(0.86, 0.96, 1.02);
      const hair = new THREE.Mesh(hairGeo, darkMat);
      hair.position.set(0, 0.032, -0.015);
      headGroup.add(hair);

      mannequinGroup.add(headGroup);

      // 2. SCULPTED NECK & CLAVICLES
      const neckGroup = new THREE.Group();
      neckGroup.position.set(0, 1.66, 0);

      const neckGeo = new THREE.CylinderGeometry(0.046 * wMult, 0.058 * wMult, 0.15, 32);
      const neck = new THREE.Mesh(neckGeo, bodyMat);
      neck.rotation.x = 0.05;
      neckGroup.add(neck);

      [-1, 1].forEach(side => {
        const scm = new THREE.Mesh(new THREE.CylinderGeometry(0.012 * wMult, 0.014 * wMult, 0.13, 16), bodyMat);
        scm.position.set(side * 0.032 * wMult, -0.01, 0.018);
        scm.rotation.z = side * 0.18;
        scm.rotation.x = 0.06;
        neckGroup.add(scm);
      });

      const clavicleGeo = new THREE.SphereGeometry(0.092 * wMult, 20, 20);
      clavicleGeo.scale(1.45, 0.16, 0.32);
      const clavicle = new THREE.Mesh(clavicleGeo, bodyMat);
      clavicle.position.set(0, -0.065, 0.03);
      neckGroup.add(clavicle);

      const choker = new THREE.Mesh(new THREE.TorusGeometry(0.056 * wMult, 0.0055, 14, 32), goldMat);
      choker.rotateX(Math.PI / 2);
      choker.position.set(0, -0.038, 0.005);
      neckGroup.add(choker);

      mannequinGroup.add(neckGroup);

      // 3. BARE SCULPTED MUSCULAR ATHLETIC TORSO
      const torsoGroup = new THREE.Group();
      torsoGroup.position.set(0, 1.38, 0);

      const shoulderWidth = (isFemale ? 0.38 : 0.46) * wMult;
      const chestWidth = (isFemale ? 0.35 : 0.44) * wMult;
      const waistWidth = (isFemale ? 0.25 : 0.31) * wMult;

      const torsoGeo = new THREE.CylinderGeometry(chestWidth * 0.48, waistWidth * 0.48, 0.44, 32);
      torsoGeo.scale(1.0, 1.0, 0.74);
      torsoGroup.add(new THREE.Mesh(torsoGeo, bodyMat));

      // Sculpted Muscular Pectorals
      [-1, 1].forEach(side => {
        const pecGeo = new THREE.SphereGeometry(0.082 * wMult, 24, 24);
        pecGeo.scale(1.18, 0.85, 0.46);
        const pec = new THREE.Mesh(pecGeo, bodyMat);
        pec.position.set(side * 0.076 * wMult, 0.10, 0.095 * wMult);
        pec.rotation.z = side * -0.06;
        torsoGroup.add(pec);
      });

      // Athletic 6-Pack Core
      [
        { y: 0.03, sX: 1.15, sY: 0.75, dZ: 0.098 },
        { y: -0.05, sX: 1.10, sY: 0.75, dZ: 0.092 },
        { y: -0.13, sX: 1.02, sY: 0.80, dZ: 0.086 }
      ].forEach(tier => {
        [-1, 1].forEach(side => {
          const abMesh = new THREE.Mesh(new THREE.SphereGeometry(0.030 * wMult, 18, 18), bodyMat);
          abMesh.scale.set(tier.sX, tier.sY, 0.34);
          abMesh.position.set(side * 0.036 * wMult, tier.y, tier.dZ * wMult);
          torsoGroup.add(abMesh);
        });
      });

      // Flanks & Obliques
      [-1, 1].forEach(side => {
        [-0.01, -0.07, -0.13].forEach((sY, i) => {
          const oblique = new THREE.Mesh(new THREE.SphereGeometry(0.024 * wMult, 14, 14), bodyMat);
          oblique.scale.set(0.65, 0.45, 0.95);
          oblique.position.set(side * (0.118 * wMult + (i * 0.004)), sY, 0.04 * wMult);
          oblique.rotation.z = side * -0.25;
          torsoGroup.add(oblique);
        });

        const lat = new THREE.Mesh(new THREE.SphereGeometry(0.075 * wMult, 20, 20), bodyMat);
        lat.scale.set(0.65, 1.45, 0.75);
        lat.position.set(side * 0.132 * wMult, 0.08, -0.045 * wMult);
        lat.rotation.z = side * -0.15;
        torsoGroup.add(lat);
      });

      const navel = new THREE.Mesh(new THREE.SphereGeometry(0.007, 12, 12), darkMat);
      navel.scale.set(0.7, 1.2, 0.4);
      navel.position.set(0, -0.17, 0.088 * wMult);
      torsoGroup.add(navel);

      mannequinGroup.add(torsoGroup);

      // 4. FIXED REALISTIC ANATOMICAL ARMS & COUTURE HANDS
      [-1, 1].forEach(side => {
        const armGroup = new THREE.Group();
        armGroup.position.set(side * (shoulderWidth * 0.51), 1.54, 0);

        const shoulderGeo = new THREE.SphereGeometry(0.058 * wMult, 24, 24);
        shoulderGeo.scale(1.05, 1.26, 1.05);
        const shoulder = new THREE.Mesh(shoulderGeo, bodyMat);
        shoulder.position.set(0, -0.01, 0);
        armGroup.add(shoulder);

        const upperArmGroup = new THREE.Group();
        upperArmGroup.position.set(0, -0.04, 0);
        upperArmGroup.rotation.z = side * -0.14;
        upperArmGroup.rotation.x = 0.04;

        const upperArmGeo = new THREE.CylinderGeometry(0.038 * wMult, 0.033 * wMult, 0.26, 24);
        const upperArm = new THREE.Mesh(upperArmGeo, bodyMat);
        upperArm.position.set(0, -0.13, 0);
        upperArmGroup.add(upperArm);

        const bicep = new THREE.Mesh(new THREE.SphereGeometry(0.036 * wMult, 18, 18), bodyMat);
        bicep.scale.set(0.9, 1.3, 0.95);
        bicep.position.set(side * -0.005, -0.12, 0.015);
        upperArmGroup.add(bicep);

        const elbow = new THREE.Mesh(new THREE.SphereGeometry(0.032 * wMult, 18, 18), bodyMat);
        elbow.position.set(0, -0.26, -0.005);
        upperArmGroup.add(elbow);

        const foreArmGroup = new THREE.Group();
        foreArmGroup.position.set(0, -0.26, 0);
        foreArmGroup.rotation.x = 0.09;
        foreArmGroup.rotation.z = side * 0.05;

        const foreArmGeo = new THREE.CylinderGeometry(0.032 * wMult, 0.023 * wMult, 0.25, 24);
        const foreArm = new THREE.Mesh(foreArmGeo, bodyMat);
        foreArm.position.set(0, -0.125, 0.01);
        foreArmGroup.add(foreArm);

        const forearmMuscle = new THREE.Mesh(new THREE.SphereGeometry(0.030 * wMult, 16, 16), bodyMat);
        forearmMuscle.scale.set(0.9, 1.4, 0.9);
        forearmMuscle.position.set(side * 0.008, -0.08, 0.015);
        foreArmGroup.add(forearmMuscle);

        const wrist = new THREE.Mesh(new THREE.SphereGeometry(0.021 * wMult, 16, 16), bodyMat);
        wrist.scale.set(1.15, 0.8, 0.85);
        wrist.position.set(0, -0.25, 0.01);
        foreArmGroup.add(wrist);

        if (side === -1) {
          const watch = new THREE.Mesh(new THREE.CylinderGeometry(0.024 * wMult, 0.024 * wMult, 0.018, 24), accentMat);
          watch.position.set(0, -0.23, 0.01);
          foreArmGroup.add(watch);

          const watchFace = new THREE.Mesh(new THREE.CylinderGeometry(0.009, 0.009, 0.006, 16), goldMat);
          watchFace.rotateX(Math.PI / 2);
          watchFace.position.set(0, -0.23, 0.028 * wMult);
          foreArmGroup.add(watchFace);
        }

        // Sculpted Couture Hand
        const handGroup = new THREE.Group();
        handGroup.position.set(0, -0.27, 0.01);
        handGroup.rotation.y = side * 0.12;
        handGroup.rotation.z = side * -0.06;

        const palmGeo = new THREE.SphereGeometry(0.024 * wMult, 20, 20);
        palmGeo.scale(0.85, 1.35, 0.55);
        const palm = new THREE.Mesh(palmGeo, bodyMat);
        palm.position.set(0, -0.02, 0);
        handGroup.add(palm);

        const thumbGroup = new THREE.Group();
        thumbGroup.position.set(side * -0.014 * wMult, -0.01, 0.008);
        thumbGroup.rotation.z = side * 0.38;
        thumbGroup.rotation.x = 0.22;

        const thumb = new THREE.Mesh(new THREE.CylinderGeometry(0.0052, 0.0040, 0.030, 12), bodyMat);
        thumb.position.y = -0.014;
        thumbGroup.add(thumb);

        const thumbTip = new THREE.Mesh(new THREE.SphereGeometry(0.0042, 10, 10), bodyMat);
        thumbTip.position.y = -0.030;
        thumbGroup.add(thumbTip);
        handGroup.add(thumbGroup);

        const fingerParams = [
          { x: -0.008, len: 0.040, curl: 0.16 },
          { x: -0.002, len: 0.044, curl: 0.20 },
          { x:  0.004, len: 0.039, curl: 0.24 },
          { x:  0.009, len: 0.032, curl: 0.28 }
        ];

        fingerParams.forEach((f) => {
          const fingerGroup = new THREE.Group();
          fingerGroup.position.set(f.x * wMult, -0.042, 0);
          fingerGroup.rotation.x = f.curl;

          const fShaft = new THREE.Mesh(new THREE.CylinderGeometry(0.0038, 0.0028, f.len, 12), bodyMat);
          fShaft.position.y = -f.len * 0.5;
          fingerGroup.add(fShaft);

          const fTip = new THREE.Mesh(new THREE.SphereGeometry(0.0030, 10, 10), bodyMat);
          fTip.position.y = -f.len;
          fingerGroup.add(fTip);

          handGroup.add(fingerGroup);
        });

        foreArmGroup.add(handGroup);
        upperArmGroup.add(foreArmGroup);
        armGroup.add(upperArmGroup);
        mannequinGroup.add(armGroup);
      });

      // 5. STRIPPED DOWN TO ONLY BOXERS
      const hipsWidth = (isFemale ? 0.38 : 0.35) * wMult;
      const legSpread = (isFemale ? 0.115 : 0.125) * wMult;

      const boxersGroup = new THREE.Group();
      boxersGroup.position.set(0, 1.08, 0);

      const waistbandGeo = new THREE.CylinderGeometry(waistWidth * 0.505, waistWidth * 0.51, 0.044, 32);
      waistbandGeo.scale(1.0, 1.0, 0.77);
      const waistband = new THREE.Mesh(waistbandGeo, waistbandMat);
      waistband.position.set(0, 0.075, 0);
      boxersGroup.add(waistband);

      const brandPatch = new THREE.Mesh(new THREE.SphereGeometry(0.015, 16, 16), goldMat);
      brandPatch.scale.set(1.4, 0.7, 0.25);
      brandPatch.position.set(0, 0.075, 0.108 * wMult);
      boxersGroup.add(brandPatch);

      const boxerTrunkGeo = new THREE.CylinderGeometry(waistWidth * 0.495, hipsWidth * 0.51, 0.18, 32);
      boxerTrunkGeo.scale(1.0, 1.0, 0.78);
      const boxerTrunk = new THREE.Mesh(boxerTrunkGeo, boxerMat);
      boxersGroup.add(boxerTrunk);

      [-1, 1].forEach(side => {
        const glute = new THREE.Mesh(new THREE.SphereGeometry(0.082 * wMult, 20, 20), boxerMat);
        glute.scale.set(1.02, 1.0, 0.85);
        glute.position.set(side * 0.072 * wMult, -0.03, -0.055 * wMult);
        boxersGroup.add(glute);
      });

      const contourPouch = new THREE.Mesh(new THREE.SphereGeometry(0.052 * wMult, 20, 20), boxerMat);
      contourPouch.scale.set(0.85, 1.30, 0.95);
      contourPouch.position.set(0, -0.04, 0.092 * wMult);
      boxersGroup.add(contourPouch);

      [-1, 1].forEach(side => {
        const boxerLegGeo = new THREE.CylinderGeometry(0.084 * wMult, 0.078 * wMult, 0.15, 28);
        boxerLegGeo.scale(1.0, 1.0, 0.88);
        const boxerLeg = new THREE.Mesh(boxerLegGeo, boxerMat);
        boxerLeg.position.set(side * legSpread, -0.13, 0);
        boxersGroup.add(boxerLeg);

        const hemGeo = new THREE.TorusGeometry(0.078 * wMult, 0.005, 12, 28);
        hemGeo.scale(1.0, 1.0, 0.88);
        hemGeo.rotateX(Math.PI / 2);
        const hem = new THREE.Mesh(hemGeo, boxerMat);
        hem.position.set(side * legSpread, -0.20, 0);
        boxersGroup.add(hem);
      });

      mannequinGroup.add(boxersGroup);

      // 6. BARE SCULPTED LEGS & BARE FEET
      [-1, 1].forEach(side => {
        const legGroup = new THREE.Group();
        legGroup.position.set(side * legSpread, 0.95, 0);

        const bareThighGeo = new THREE.CylinderGeometry(0.076 * wMult, 0.056 * wMult, 0.38, 28);
        bareThighGeo.scale(1.0, 1.0, 0.90);
        const bareThigh = new THREE.Mesh(bareThighGeo, bodyMat);
        bareThigh.position.set(0, -0.22, 0);
        legGroup.add(bareThigh);

        const quad = new THREE.Mesh(new THREE.SphereGeometry(0.062 * wMult, 18, 18), bodyMat);
        quad.scale.set(0.9, 1.45, 0.7);
        quad.position.set(0, -0.20, 0.045 * wMult);
        legGroup.add(quad);

        const knee = new THREE.Mesh(new THREE.SphereGeometry(0.046 * wMult, 20, 20), bodyMat);
        knee.scale.set(0.92, 1.10, 0.84);
        knee.position.set(0, -0.42, 0.015);
        legGroup.add(knee);

        const lowerLegGeo = new THREE.CylinderGeometry(0.054 * wMult, 0.034 * wMult, 0.44, 28);
        lowerLegGeo.scale(1.0, 1.0, 0.85);
        const lowerLeg = new THREE.Mesh(lowerLegGeo, bodyMat);
        lowerLeg.position.set(0, -0.66, 0);
        legGroup.add(lowerLeg);

        const calf = new THREE.Mesh(new THREE.SphereGeometry(0.048 * wMult, 20, 20), bodyMat);
        calf.scale.set(0.85, 1.4, 1.05);
        calf.position.set(0, -0.58, -0.025 * wMult);
        legGroup.add(calf);

        [-1, 1].forEach(ankleSide => {
          const ankleBone = new THREE.Mesh(new THREE.SphereGeometry(0.012 * wMult, 12, 12), bodyMat);
          ankleBone.position.set(ankleSide * 0.034 * wMult, -0.87, 0);
          legGroup.add(ankleBone);
        });

        // Bare Sculpted Feet
        const footGroup = new THREE.Group();
        footGroup.position.set(0, -0.92, 0.02);

        const instepGeo = new THREE.SphereGeometry(0.038 * wMult, 20, 20);
        instepGeo.scale(0.85, 0.70, 1.45);
        const instep = new THREE.Mesh(instepGeo, bodyMat);
        instep.position.set(0, 0.022, 0.015);
        footGroup.add(instep);

        const heel = new THREE.Mesh(new THREE.SphereGeometry(0.030 * wMult, 16, 16), bodyMat);
        heel.position.set(0, 0.016, -0.045);
        footGroup.add(heel);

        const ballOfFoot = new THREE.Mesh(new THREE.SphereGeometry(0.036 * wMult, 18, 18), bodyMat);
        ballOfFoot.scale.set(1.05, 0.55, 1.15);
        ballOfFoot.position.set(0, 0.012, 0.075);
        footGroup.add(ballOfFoot);

        const toeCrest = new THREE.Mesh(new THREE.CylinderGeometry(0.018 * wMult, 0.028 * wMult, 0.055, 16), bodyMat);
        toeCrest.rotateX(Math.PI / 2);
        toeCrest.scale.set(1.15, 1.0, 0.55);
        toeCrest.position.set(side * 0.005, 0.010, 0.115);
        footGroup.add(toeCrest);

        legGroup.add(footGroup);
        mannequinGroup.add(legGroup);
      });

      // Runway Podium
      const ped = new THREE.Mesh(new THREE.CylinderGeometry(0.85, 0.92, 0.06, 36), new THREE.MeshStandardMaterial({ color: 0x18181A, roughness: 0.3, metalness: 0.2 }));
      ped.position.y = 0;
      mannequinGroup.add(ped);

      const goldRim = new THREE.Mesh(new THREE.TorusGeometry(0.88, 0.014, 16, 36), goldMat);
      goldRim.rotateX(Math.PI / 2);
      goldRim.position.y = 0.03;
      mannequinGroup.add(goldRim);
    }

    function applyComboToStudio(topC, botC, shoeC) {
      buildMannequin(topC, botC, shoeC);
      showToast("Applied combo to 3D Mannequin!");
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function toggleGender() {
      currentGender = (currentGender === 'male') ? 'female' : 'male';
      document.getElementById('gender-label').textContent = currentGender.charAt(0).toUpperCase() + currentGender.slice(1);
      mannequinGroup.scale.set(currentGender === 'female' ? 0.92 : 1, currentGender === 'female' ? 0.96 : 1, currentGender === 'female' ? 0.92 : 1);
    }

    function cycleBody() {
      const bodies = ['Slim', 'Athletic', 'Broad', 'Plus'];
      const idx = (bodies.indexOf(currentBody) + 1) % bodies.length;
      currentBody = bodies[idx];
      document.getElementById('body-label').textContent = currentBody;
      const factor = currentBody === 'Slim' ? 0.9 : currentBody === 'Athletic' ? 1.05 : currentBody === 'Broad' ? 1.2 : 1.35;
      if (topMesh) topMesh.scale.set(factor, 1, factor);
    }

    function rotateStudio() {
      mannequinGroup.rotation.y += Math.PI * 0.5;
    }

    function renderSkinSwatches() {
      const container = document.getElementById('skin-swatches-container');
      if (!container) return;
      container.innerHTML = skinTonePresets.map(function(t) {
        var activeClass = (t.hex.toLowerCase() === currentSkinTone.toLowerCase()) ? ' active' : '';
        return '<div class="skin-swatch-item' + activeClass + '" style="background: ' + t.hex + ';" title="' + t.name + ' (' + t.hex + ')" onclick="selectSkinToneWeb(\'' + t.hex + '\', \'' + t.name + '\')"></div>';
      }).join('');
    }

    function selectSkinToneWeb(hex, name) {
      currentSkinTone = hex;
      const label = document.getElementById('skin-label-name');
      if (label) label.textContent = name;
      renderSkinSwatches();
      buildMannequin(currentTopColor, currentBottomColor, currentShoesColor, currentSkinTone);
      showToast("Complexion adjusted to " + name);
    }

    // Init
    window.addEventListener('DOMContentLoaded', () => {
      renderBrands();
      init3DStudio();
      renderSkinSwatches();
    });
  </script>
</body>
</html>`;
}

const server = http.createServer((req, res) => {
  const url = req.url.split('?')[0];

  if (url === '/' || url === '/index.html') {
    res.writeHead(200, {
      'Content-Type': 'text/html; charset=utf-8',
      'Cache-Control': 'no-cache'
    });
    res.end(getHtml());
  } else if (url === '/manifest.json') {
    res.writeHead(200, {
      'Content-Type': 'application/json',
      'Cache-Control': 'public, max-age=86400'
    });
    res.end(JSON.stringify(MANIFEST));
  } else if (url === '/api/brands') {
    res.writeHead(200, {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*'
    });
    res.end(JSON.stringify(BRANDS));
  } else if (url === '/health') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('OK');
  } else {
    // Fallback to HTML for single-page routing
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(getHtml());
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Atelier] Web server running on http://0.0.0.0:${PORT}`);
});
