# 445p3

## IntCoin: a *very* simple cryptocurrency 

### Encryption:
Messages are encrypted using RSA and AES. 

### P2P:
IntCoin uses P2P communication (this is not entirely clear from our naming scheme). Clients (peers) broadcast their desired transactions to all other peers. When a transaction request is received, a validation is attempted, and if successful is added to the local block. Once the block is full of valid transactions, an attempt is made to mine it and publish it to the persistent blockchain. If this succeeds, all other clients are notified to update their local chains. If mining and publishing of a block fails, the creator of the block updates their own chain and starts a new block. 

### Blockchain:
The blockchain consists of blocks hashed with SHA-256. Nonce difficulty is adjustable. Each block consists of its transaction data, updated balance totals, previous block hash (proof of work), and a timestamp.  


