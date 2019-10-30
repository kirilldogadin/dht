package global.unet.id;

/**
 * Eng:
 * Id in network.
 * Maybe:
 * UnionId - unique Id in one Union network. Example "aa80"
 * GlobalId - full networkId in global network. Example "union.global#aa80"
 * Uname - alias of UnionId. Need resolving for becomming networkId.
 * Short Uname - resolve become UnionId "myContent" return "aa80"
 * Full Uname - resolve become Global networkId "union.Global#myContent" return GlobalId "union.Global#aa80"
 *
 * Родительский тип
 */
public interface BaseId {
}
