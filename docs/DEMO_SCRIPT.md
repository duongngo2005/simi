# Simi — Demo and regression checklist

This checklist is intentionally small. It proves the main consignment-sales flow and
the dangerous paths that the backend must reject; it is not a load or enterprise test plan.

## Before starting

1. Start MySQL, the Spring Boot backend, and the Vite frontend.
2. Use three separate accounts: `staff`, `customer A`, and `customer B`.
3. Create a new draft consignment owned by customer A. Add one item with a day-0
   price and at least one product image.
4. Record the new consignment, item, product, order, and settlement IDs here while
   running the demo. Do not reuse a product that was already sold or expired.

| Entity | ID used in this run |
| --- | --- |
| Consignment | |
| Consignment item | |
| Product | |
| COD order | |
| Settlement | |

## Core demo: consignment to settlement

| Checkpoint | Action | UI/API expected result | Expected DB state |
| --- | --- | --- | --- |
| 1. Receive | Staff creates the draft consignment and item. | The item can still be edited by staff. | `Consignment=DRAFT`, `ConsignmentItem=DRAFT`, `Product=DRAFT`. |
| 2. Activate | Staff activates the consignment. | Product appears on `/products` and its detail page enables add-to-cart/buy-now. | `Consignment=ACTIVE`, `ConsignmentItem=ACTIVE`, `Product=AVAILABLE`, `current_price` equals the day-0 price. |
| 3. Cart | Customer A adds that product to cart. | Cart displays one line with the listed price. | Cart belongs to customer A; product is still `AVAILABLE` until checkout. |
| 4. Checkout | Customer A creates one **COD** online order. | Order is created and the selected cart item is removed. | `Order=PENDING`, `Payment=PENDING` with method `COD`; `OrderItem.unit_price` snapshots the listed price; `Product=RESERVED`; `ConsignmentItem=RESERVED`. |
| 5. Fulfil | Staff changes order to `PACKING`, then `SHIPPING`, then `COMPLETED`. | Each valid next action is available; no step is skipped. | `Order=COMPLETED`, `Payment=PAID`, `Product=SOLD`, `ConsignmentItem=SOLD`. |
| 6. Settlement | Staff creates/processes the settlement for the completed consignment. | Payout is visible to customer A and uses the order's sold price. | `Consignment=SETTLED`; settlement amounts are calculated from `OrderItem.unit_price`, not `Product.current_price`. |
| 7. Close | Complete any outstanding disposition, if the consignment has unsold/expired items. | Staff can close only after all required dispositions are complete. | `Consignment=CLOSED`; every item has a final status such as `SOLD`, `RETURNED`, `DONATED`, or `CANCELLED`. |

## Negative-path regression

Run these with direct API calls as well as the UI where practical. A frontend button
being hidden or disabled is not proof; the backend response is authoritative.

| Scenario | Expected result |
| --- | --- |
| Customer B reads Customer A's order, consignment, or settlement. | Reject with `403` (or `404` for a resource that genuinely does not exist). No data is returned. |
| Customer B edits Customer A's consignment item. | Reject with `403`. |
| Product is `SOLD` or `EXPIRED` and is added to cart/checkout/POS. | Reject; no new order item is created. |
| Consignment item is not `ACTIVE` at checkout. | Reject; product is not reserved. |
| Online checkout requests `CASH`. | Reject; only `COD` and `ONLINE` are accepted online. |
| Client sends a non-zero discount. | Reject; the backend does not trust a client-side discount. |
| Unpaid online order changes to `PACKING` or `COMPLETED`. | Reject. |
| Paid online order is cancelled directly. | Reject because gateway refund is not implemented. |
| Product has a price markdown after it was sold. | Settlement payout remains based on `OrderItem.unit_price`. |
| A reservation expires. | Reservation is released or expires according to the consignment state; it never leaves a buyable item stuck in `RESERVED`. |

## VNPAY smoke check (optional for the core demo)

Use VNPAY sandbox credentials only. Do not make a real payment for the presentation.

1. Create an online order and verify the generated payment URL has a pending payment attempt.
2. Send/receive one valid sandbox IPN and verify `Payment=PAID` and the order can move to `PACKING`.
3. Repeat that IPN and verify it is idempotent.
4. Send a late successful IPN after expiry and verify it does not reactivate the order.

The automated `PaymentServiceTest` covers the latter two cases; the sandbox step is
only a configuration and callback smoke test.

## Phase F is done only when

- `cd simi-be && ./mvnw test` passes.
- `cd simi-fe && npm run build` passes.
- The core COD flow above succeeds through FE → BE → DB.
- Every negative-path row above is rejected by the backend.

If a check fails, make the smallest direct fix, add one regression test beside the
affected service/mapper, then repeat this checklist. Do not add unrelated features
or refactor modules during Phase F.
