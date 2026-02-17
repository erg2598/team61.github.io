CREATE TABLE IF NOT EXISTS public."Order"
(
    "orderId" integer NOT NULL,
    "orderLineId" integer NOT NULL,
    "orderDate" date NOT NULL,
    status character varying(20) DEFAULT 'NOT READY',
    "totalAmount" numeric(10, 2) DEFAULT 0,
    "customerName" character varying(100) DEFAULT 'ANONYMOUS',
    PRIMARY KEY ("orderId")
);

COMMENT ON TABLE public."Order" IS 'Order Overview Information';

CREATE TABLE IF NOT EXISTS public."OrderLineItem"
(
    "orderLineId" integer NOT NULL,
    "orderId" integer NOT NULL,
    "itemId" integer NOT NULL,
    quantity integer DEFAULT 0,
    "sugarAmount" real DEFAULT 0,
    "iceLevel" character varying(20) DEFAULT 'NONE',
    PRIMARY KEY ("orderLineId")
);

COMMENT ON TABLE public."OrderLineItem" IS 'The Specific Customized Item';

CREATE TABLE IF NOT EXISTS public."Item"
(
    "itemId" integer NOT NULL,
    name character varying(50) NOT NULL,
    "basePrice" numeric(10, 2) DEFAULT 0,
    size character varying(20) DEFAULT 'SMALL',
    PRIMARY KEY ("itemId")
);

COMMENT ON TABLE public."Item" IS 'for each general menu item.';

CREATE TABLE IF NOT EXISTS public."Ingredients"
(
    "ingredientId" integer NOT NULL,
    "itemId" integer NOT NULL,
    "inventoryId" integer NOT NULL,
    "quantityUsed" real DEFAULT 0,
    PRIMARY KEY ("ingredientId")
);

COMMENT ON TABLE public."Ingredients" IS 'holds the different ingredients used for each menu item.';

CREATE TABLE IF NOT EXISTS public."Inventory"
(
    "inventoryId" integer NOT NULL,
    name character varying(50) NOT NULL,
    "quantityOnHand" real DEFAULT 0,
    "reorderThreshold" integer,
    PRIMARY KEY ("inventoryId")
);

COMMENT ON TABLE public."Inventory"
    IS 'Holds the ingredients and the amounts of each.';