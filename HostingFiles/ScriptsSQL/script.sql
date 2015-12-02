--
-- Table structure for table `keysvotes`
--

CREATE TABLE IF NOT EXISTS `keysvotes` (
  `idvotation` bigint(10) NOT NULL,
  `publicKey` varchar(2048) NOT NULL,
  `privateKey` varchar(2048) NOT NULL,
  PRIMARY KEY (`idvotation`),
  UNIQUE KEY `idvotation` (`idvotation`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `token` (
  `idvotation` int(9) NOT NULL,
  `accesstoken` int(11) NOT NULL,
  PRIMARY KEY (`idvotation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
