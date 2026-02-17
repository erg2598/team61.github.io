CREATE TABLE IF NOT EXISTS public."Order"
(
    "orderId" SERIAL,
    "orderDate" timestamp NOT NULL,
    status character varying(20) DEFAULT 'NOT READY',
    "totalAmount" numeric(10, 2) DEFAULT 0,
    "customerName" character varying(100) DEFAULT 'ANONYMOUS',
    PRIMARY KEY ("orderId")
);

COMMENT ON TABLE public."Order" IS 'Order Overview Information';

CREATE TABLE IF NOT EXISTS public."OrderLineItem"
(
    "orderLineId" SERIAL,
    "orderId" integer NOT NULL,
    "itemId" integer NOT NULL,
    quantity integer DEFAULT 0,
    "sugarAmount" integer DEFAULT 0,
    "iceLevel" character varying(20) DEFAULT 'NONE',
    PRIMARY KEY ("orderLineId")
);

COMMENT ON TABLE public."OrderLineItem" IS 'The Specific Customized Item';

CREATE TABLE IF NOT EXISTS public."Item"
(
    "itemId" SERIAL,
    name character varying(50) NOT NULL,
    "basePrice" real DEFAULT 0,
    size character varying(20) DEFAULT 'SMALL',
    PRIMARY KEY ("itemId")
);

COMMENT ON TABLE public."Item" IS 'for each general menu item.';

CREATE TABLE IF NOT EXISTS public."Ingredients"
(
    "ingredientId" SERIAL,
    "itemId" integer NOT NULL,
    "inventoryId" integer NOT NULL,
    "quantityUsed" real DEFAULT 0,
    PRIMARY KEY ("ingredientId")
);

COMMENT ON TABLE public."Ingredients" IS 'holds the different ingredients used for each menu item.';

CREATE TABLE IF NOT EXISTS public."Inventory"
(
    "inventoryId" SERIAL,
    name character varying(50) NOT NULL,
    "quantityOnHand" real DEFAULT 0,
    "reorderThreshold" integer,
    PRIMARY KEY ("inventoryId")
);

COMMENT ON TABLE public."Inventory" IS 'Holds the ingredients and the amounts of each.';

ALTER TABLE IF EXISTS public."OrderLineItem"
    ADD FOREIGN KEY ("orderId")
    REFERENCES public."Order" ("orderId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."OrderLineItem"
    ADD FOREIGN KEY ("itemId")
    REFERENCES public."Item" ("itemId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."Ingredients"
    ADD FOREIGN KEY ("itemId")
    REFERENCES public."Item" ("itemId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."Ingredients"
    ADD FOREIGN KEY ("inventoryId")
    REFERENCES public."Inventory" ("inventoryId") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;