Weird stuff

- Max number of words was supposed to be 201 but in block 71036 it has a script with 4006 words
- Scripts shouldn't be 0 bytes but:
  - Block 211914 has a coinbase script and a non-coinbase scripts that are 0 bytes long

- Block 171 is the first block that has a non-generation output transaction

Block 10 has the transaction that block 171 refers to
Block 171 - prev hash - c9 97 a5 e5 6e 10 41 02 fa 20 9c 6a 85 2d d9 06 60 a2 0b 2d 9c 35 24 23 ed ce 25 85 7f cd 37 04
                 output index - 0 - unlock the coin base of the block with this merkle root
Block 10 - merkle root - c9 97 a5 e5 6e 10 41 02 fa 20 9c 6a 85 2d d9 06 60 a2 0b 2d 9c 35 24 23 ed ce 25 85 7f cd 37 04