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