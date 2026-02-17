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