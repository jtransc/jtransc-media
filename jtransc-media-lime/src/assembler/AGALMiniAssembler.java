package assembler;

import flash.display3D.Context3DProgramType;
import flash.utils.ByteArray;

import java.util.HashMap;
import java.util.Map;

public class AGALMiniAssembler extends AGALMiniAssemblerConsts {
    public ByteArray assemble(Context3DProgramType type, String code) {
        throw new RuntimeException("AGALMiniAssembler not implemented!");
    }

    void initregmap(int version, boolean ignorelimits) {
        // version changes limits
        REGMAP.put(VA, new Register(VA, "vertex attribute", 0x0, ignorelimits ? 1024 : 7, REG_VERT | REG_READ));
        REGMAP.put(VC, new Register(VC, "vertex constant", 0x1, ignorelimits ? 1024 : (version == 1 ? 127 : 250), REG_VERT | REG_READ));
        REGMAP.put(VT, new Register(VT, "vertex temporary", 0x2, ignorelimits ? 1024 : (version == 1 ? 7 : 27), REG_VERT | REG_WRITE | REG_READ));
        REGMAP.put(VO, new Register(VO, "vertex output", 0x3, ignorelimits ? 1024 : 0, REG_VERT | REG_WRITE));
        REGMAP.put(VI, new Register(VI, "varying", 0x4, ignorelimits ? 1024 : (version == 1 ? 7 : 11), REG_VERT | REG_FRAG | REG_READ | REG_WRITE));
        REGMAP.put(FC, new Register(FC, "fragment constant", 0x1, ignorelimits ? 1024 : (version == 1 ? 27 : 63), REG_FRAG | REG_READ));
        REGMAP.put(FT, new Register(FT, "fragment temporary", 0x2, ignorelimits ? 1024 : (version == 1 ? 7 : 27), REG_FRAG | REG_WRITE | REG_READ));
        REGMAP.put(FS, new Register(FS, "texture sampler", 0x5, ignorelimits ? 1024 : 7, REG_FRAG | REG_READ));
        REGMAP.put(FO, new Register(FO, "fragment output", 0x3, ignorelimits ? 1024 : (version == 1 ? 0 : 3), REG_FRAG | REG_WRITE));
        REGMAP.put(FD, new Register(FD, "fragment depth output", 0x6, ignorelimits ? 1024 : (version == 1 ? -1 : 0), REG_FRAG | REG_WRITE));

        // aliases
        REGMAP.put("op", REGMAP.get(VO));
        REGMAP.put("i", REGMAP.get(VI));
        REGMAP.put("v", REGMAP.get(VI));
        REGMAP.put("oc", REGMAP.get(FO));
        REGMAP.put("od", REGMAP.get(FD));
        REGMAP.put("fi", REGMAP.get(VI));
    }

    static void initOnce() {
        if (initialized) return;
        initialized = true;

        // Fill the dictionaries with opcodes and registers
        OPMAP.put(MOV, new OpCode(MOV, 2, 0x00, 0));
        OPMAP.put(ADD, new OpCode(ADD, 3, 0x01, 0));
        OPMAP.put(SUB, new OpCode(SUB, 3, 0x02, 0));
        OPMAP.put(MUL, new OpCode(MUL, 3, 0x03, 0));
        OPMAP.put(DIV, new OpCode(DIV, 3, 0x04, 0));
        OPMAP.put(RCP, new OpCode(RCP, 2, 0x05, 0));
        OPMAP.put(MIN, new OpCode(MIN, 3, 0x06, 0));
        OPMAP.put(MAX, new OpCode(MAX, 3, 0x07, 0));
        OPMAP.put(FRC, new OpCode(FRC, 2, 0x08, 0));
        OPMAP.put(SQT, new OpCode(SQT, 2, 0x09, 0));
        OPMAP.put(RSQ, new OpCode(RSQ, 2, 0x0a, 0));
        OPMAP.put(POW, new OpCode(POW, 3, 0x0b, 0));
        OPMAP.put(LOG, new OpCode(LOG, 2, 0x0c, 0));
        OPMAP.put(EXP, new OpCode(EXP, 2, 0x0d, 0));
        OPMAP.put(NRM, new OpCode(NRM, 2, 0x0e, 0));
        OPMAP.put(SIN, new OpCode(SIN, 2, 0x0f, 0));
        OPMAP.put(COS, new OpCode(COS, 2, 0x10, 0));
        OPMAP.put(CRS, new OpCode(CRS, 3, 0x11, 0));
        OPMAP.put(DP3, new OpCode(DP3, 3, 0x12, 0));
        OPMAP.put(DP4, new OpCode(DP4, 3, 0x13, 0));
        OPMAP.put(ABS, new OpCode(ABS, 2, 0x14, 0));
        OPMAP.put(NEG, new OpCode(NEG, 2, 0x15, 0));
        OPMAP.put(SAT, new OpCode(SAT, 2, 0x16, 0));
        OPMAP.put(M33, new OpCode(M33, 3, 0x17, OP_SPECIAL_MATRIX));
        OPMAP.put(M44, new OpCode(M44, 3, 0x18, OP_SPECIAL_MATRIX));
        OPMAP.put(M34, new OpCode(M34, 3, 0x19, OP_SPECIAL_MATRIX));
        OPMAP.put(IFZ, new OpCode(IFZ, 1, 0x1a, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(INZ, new OpCode(INZ, 1, 0x1b, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(IFE, new OpCode(IFE, 2, 0x1c, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(INE, new OpCode(INE, 2, 0x1d, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(IFG, new OpCode(IFG, 2, 0x1e, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(IFL, new OpCode(IFL, 2, 0x1f, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(IEG, new OpCode(IEG, 2, 0x20, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(IEL, new OpCode(IEL, 2, 0x21, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(ELS, new OpCode(ELS, 0, 0x22, OP_NO_DEST | OP_INC_NEST | OP_DEC_NEST));
        OPMAP.put(EIF, new OpCode(EIF, 0, 0x23, OP_NO_DEST | OP_DEC_NEST));
        OPMAP.put(REP, new OpCode(REP, 1, 0x24, OP_NO_DEST | OP_INC_NEST | OP_SCALAR));
        OPMAP.put(ERP, new OpCode(ERP, 0, 0x25, OP_NO_DEST | OP_DEC_NEST));
        OPMAP.put(BRK, new OpCode(BRK, 0, 0x26, OP_NO_DEST));
        OPMAP.put(KIL, new OpCode(KIL, 1, 0x27, OP_NO_DEST | OP_FRAG_ONLY));
        OPMAP.put(TEX, new OpCode(TEX, 3, 0x28, OP_FRAG_ONLY | OP_SPECIAL_TEX));
        OPMAP.put(SGE, new OpCode(SGE, 3, 0x29, 0));
        OPMAP.put(SLT, new OpCode(SLT, 3, 0x2a, 0));
        OPMAP.put(SGN, new OpCode(SGN, 2, 0x2b, 0));

        SAMPLEMAP.put(D2, new Sampler(D2, SAMPLER_DIM_SHIFT, 0));
        SAMPLEMAP.put(D3, new Sampler(D3, SAMPLER_DIM_SHIFT, 2));
        SAMPLEMAP.put(CUBE, new Sampler(CUBE, SAMPLER_DIM_SHIFT, 1));
        SAMPLEMAP.put(MIPNEAREST, new Sampler(MIPNEAREST, SAMPLER_MIPMAP_SHIFT, 1));
        SAMPLEMAP.put(MIPLINEAR, new Sampler(MIPLINEAR, SAMPLER_MIPMAP_SHIFT, 2));
        SAMPLEMAP.put(MIPNONE, new Sampler(MIPNONE, SAMPLER_MIPMAP_SHIFT, 0));
        SAMPLEMAP.put(NOMIP, new Sampler(NOMIP, SAMPLER_MIPMAP_SHIFT, 0));
        SAMPLEMAP.put(NEAREST, new Sampler(NEAREST, SAMPLER_FILTER_SHIFT, 0));
        SAMPLEMAP.put(LINEAR, new Sampler(LINEAR, SAMPLER_FILTER_SHIFT, 1));
        SAMPLEMAP.put(CENTROID, new Sampler(CENTROID, SAMPLER_SPECIAL_SHIFT, 1 << 0));
        SAMPLEMAP.put(SINGLE, new Sampler(SINGLE, SAMPLER_SPECIAL_SHIFT, 1 << 1));
        SAMPLEMAP.put(DEPTH, new Sampler(DEPTH, SAMPLER_SPECIAL_SHIFT, 1 << 2));
        SAMPLEMAP.put(REPEAT, new Sampler(REPEAT, SAMPLER_REPEAT_SHIFT, 1));
        SAMPLEMAP.put(WRAP, new Sampler(WRAP, SAMPLER_REPEAT_SHIFT, 1));
        SAMPLEMAP.put(CLAMP, new Sampler(CLAMP, SAMPLER_REPEAT_SHIFT, 0));
    }

}

class AGALMiniAssemblerConsts {

    static protected boolean initialized = false;

    static final Map<String, OpCode> OPMAP = new HashMap<String, OpCode>();
    static final Map<String, Register> REGMAP = new HashMap<String, Register>();
    static final Map<String, Sampler> SAMPLEMAP = new HashMap<String, Sampler>();

    static final int MAX_NESTING = 4;
    static final int MAX_OPCODES = 256;

    // masks and shifts
    static final int SAMPLER_DIM_SHIFT = 12;
    static final int SAMPLER_SPECIAL_SHIFT = 16;
    static final int SAMPLER_REPEAT_SHIFT = 20;
    static final int SAMPLER_MIPMAP_SHIFT = 24;
    static final int SAMPLER_FILTER_SHIFT = 28;

    // regmap flags
    static final int REG_WRITE = 0x1;
    static final int REG_READ = 0x2;
    static final int REG_FRAG = 0x20;
    static final int REG_VERT = 0x40;

    // opmap flags
    static final int OP_SCALAR = 0x1;
    static final int OP_INC_NEST = 0x2;
    static final int OP_DEC_NEST = 0x4;
    static final int OP_SPECIAL_TEX = 0x8;
    static final int OP_SPECIAL_MATRIX = 0x10;
    static final int OP_FRAG_ONLY = 0x20;
    static final int OP_VERT_ONLY = 0x40;
    static final int OP_NO_DEST = 0x80;

    // opcodes
    static final String MOV = "mov";
    static final String ADD = "add";
    static final String SUB = "sub";
    static final String MUL = "mul";
    static final String DIV = "div";
    static final String RCP = "rcp";
    static final String MIN = "min";
    static final String MAX = "max";
    static final String FRC = "frc";
    static final String SQT = "sqt";
    static final String RSQ = "rsq";
    static final String POW = "pow";
    static final String LOG = "log";
    static final String EXP = "exp";
    static final String NRM = "nrm";
    static final String SIN = "sin";
    static final String COS = "cos";
    static final String CRS = "crs";
    static final String DP3 = "dp3";
    static final String DP4 = "dp4";
    static final String ABS = "abs";
    static final String NEG = "neg";
    static final String SAT = "sat";
    static final String M33 = "m33";
    static final String M44 = "m44";
    static final String M34 = "m34";
    static final String IFZ = "ifz";
    static final String INZ = "inz";
    static final String IFE = "ife";
    static final String INE = "ine";
    static final String IFG = "ifg";
    static final String IFL = "ifl";
    static final String IEG = "ieg";
    static final String IEL = "iel";
    static final String ELS = "els";
    static final String EIF = "eif";
    static final String REP = "rep";
    static final String ERP = "erp";
    static final String BRK = "brk";
    static final String KIL = "kil";
    static final String TEX = "tex";
    static final String SGE = "sge";
    static final String SLT = "slt";
    static final String SGN = "sgn";

    // registers
    static final String VA = "va";
    static final String VC = "vc";
    static final String VT = "vt";
    static final String VO = "vo";
    static final String VI = "vi";
    static final String FC = "fc";
    static final String FT = "ft";
    static final String FS = "fs";
    static final String FO = "fo";
    static final String FD = "fd";
    // samplers
    static final String D2 = "2d";
    static final String D3 = "3d";
    static final String CUBE = "cube";
    static final String MIPNEAREST = "mipnearest";
    static final String MIPLINEAR = "miplinear";
    static final String MIPNONE = "mipnone";
    static final String NOMIP = "nomip";
    static final String NEAREST = "nearest";
    static final String LINEAR = "linear";
    static final String CENTROID = "centroid";
    static final String SINGLE = "single";
    static final String DEPTH = "depth";
    static final String REPEAT = "repeat";
    static final String WRAP = "wrap";
    static final String CLAMP = "clamp";
}

/*

// Original haxe port : https://github.com/KTXSoftware/haxe-graphicscorelib
// Cleaned & updated by Thomas Hourdel
class AGALMiniAssembler
{
	static var trimReg:EReg = ~/^\s+|\s+$/g;
	static var initialized:Bool = false;

	public var agalcode(default, null):ByteArray;
	public var error(default, null):String;

	var debugEnabled:Bool;

	public function new(debugging:Bool = false)
	{
		agalcode = null;
		error = "";
		debugEnabled = debugging;

		if (!initialized)
			init();
	}

   static public function matchAll(r:EReg, str:String) {
        var matches = [];
        var offset = 0;
        while (r.match(str.substr(offset))) {
            //trace(str);
            var roffset = r.matchedPos();

            var pos = r.matchedPos().pos + offset;
            var len = r.matchedPos().len;

            matches.push(str.substr(pos, len));

            offset += roffset.pos + roffset.len;
        }

        return matches;
    }

	public function assemble(mode:Context3DProgramType, source:String, verbose:Bool = false, version:Int = 1, ignorelimits:Bool = false):ByteArray
	{
		var start:Int = Lib.getTimer();

		agalcode = new ByteArray();
		error = "";

		var isFrag:Bool = false;

		if (mode == Context3DProgramType.FRAGMENT)
			isFrag = true;

		agalcode.endian = Endian.LITTLE_ENDIAN;
		agalcode.writeByte(0xa0);            // tag version
		agalcode.writeUnsignedInt(0x1);      // AGAL version, big endian, bit pattern will be 0x01000000
		agalcode.writeByte(0xa1);            // tag program id
		agalcode.writeByte(isFrag ? 1 : 0);  // vertex or fragment

		initregmap(version, ignorelimits);

		var reg = ~/[\n\r]+/g;
		var lines = reg.replace(source, "\n").split("\n");
		var nest = 0;
		var nops = 0;
		var lng = lines.length;

		var i = 0;
		while (i < lng && error == "")
		{
			var line = new String(lines[i]);
			line = trimReg.replace(line, "");

			// remove comments
			var startcomment = line.indexOf("//");

			if (startcomment != -1)
				line = line.substr(0, startcomment);

			// grab options
			reg = ~/<.*>/g;
			var optsi:Int = -1, options:String = new String(line);

			if (reg.match(options))
				optsi = reg.matchedPos().pos;

			var opts:Array<String> = [];

			if (optsi != -1)
			{
				options = line.substr(optsi);
				line = line.substr(0, optsi);
				for (part in matchAll(~/\w+/, options)) {
					opts.push(part);
				}
			}

			// find opcode
			reg = ~/^\w{3}/ig;
			reg.match(line);
			var opCode = reg.matched(0);
			var opFound = OPMAP.get(opCode);

			// if debug is enabled, output the opcodes
			if (debugEnabled)
				Lib.trace(opFound);

			if (opFound == null)
			{
				if (line.length >= 3)
					Lib.trace("warning: bad line " + i + ": " + lines[i]);

				i++;
				continue;
			}

			line = line.substr(line.indexOf(opFound.name) + opFound.name.length);

			// nesting check
			if ((opFound.flags & OP_DEC_NEST) != 0)
			{
				nest--;

				if (nest < 0)
				{
					error = "error: conditional closes without open.";
					break;
				}
			}

			if ((opFound.flags & OP_INC_NEST) != 0)
			{
				nest++;

				if (nest > MAX_NESTING)
				{
					error = "error: nesting to deep, maximum allowed is " + MAX_NESTING + ".";
					break;
				}
			}

			if ((opFound.flags & OP_VERT_ONLY) != 0 && isFrag)
			{
				error = "error: opcode is only allowed in vertex programs.";
				break;
			}

			if ((opFound.flags & OP_FRAG_ONLY) != 0 && !isFrag)
			{
				error = "error: opcode is only allowed in fragment programs.";
				break;
			}

			if (verbose)
				Lib.trace("emit opcode=" + opFound);

			agalcode.writeUnsignedInt(opFound.emitCode);
			nops++;

			if (nops > MAX_OPCODES)
			{
				error = "error: too many opcodes. maximum is " + MAX_OPCODES + ".";
				break;
			}

			// get operands, use regexp
			reg = ~/vc\[([vof][actps]?)(\d*)?(\.[xyzw](\+\d{1,3})?)?\](\.[xyzw]{1,4})?|([vof][actps]?)(\d*)?(\.[xyzw]{1,4})?/gi;
			var subline = line;
			var regs = new Array<String>();

			while (reg.match(subline))
			{
				regs.push(reg.matched(0));
				subline = subline.substr(reg.matchedPos().pos + reg.matchedPos().len);

				if (subline.charAt(0) == ",")
					subline = subline.substr(1);

				reg = ~/vc\[([vof][actps]?)(\d*)?(\.[xyzw](\+\d{1,3})?)?\](\.[xyzw]{1,4})?|([vof][actps]?)(\d*)?(\.[xyzw]{1,4})?/gi;
			}

			if (regs.length != Std.int(opFound.numRegister))
			{
				error = "error: wrong number of operands. found " + regs.length + " but expected " + opFound.numRegister + ".";
				break;
			}

			var badreg = false;
			var pad = 64 + 64 + 32;
			var regLength = regs.length;

			var j = 0;
			while (j < Std.int(regLength))
			{
				var isRelative = false;
				reg = ~/\[.*\]/ig;
				var relreg = "";

				if (reg.match(regs[j]))
				{
					relreg = reg.matched(0);
					var relpos = source.indexOf(relreg);
					regs[j] = regs[j].substr(0, relpos) + "0" + regs[j].substr(relpos + relreg.length);

					if (verbose)
						Lib.trace("IS REL");

					isRelative = true;
				}

				reg = ~/^\b[A-Za-z]{1,2}/ig;
				reg.match(regs[j]);
				var res = reg.matched(0);
				var regFound = REGMAP.get(res);

				// if debug is enabled, output the registers
				if (debugEnabled)
					Lib.trace(regFound);

				if (regFound == null)
				{
					error = "error: could not parse operand " + j + " (" + regs[j] + ").";
					badreg = true;
					break;
				}

				if (isFrag)
				{
					if ((regFound.flags & REG_FRAG) == 0)
					{
						error = "error: register operand "+j+" ("+regs[j]+") only allowed in vertex programs.";
						badreg = true;
						break;
					}

					if (isRelative)
					{
						error = "error: register operand " + j + " (" + regs[j] + ") relative adressing not allowed in fragment programs.";
						badreg = true;
						break;
					}
				}
				else
				{
					if ((regFound.flags & REG_VERT) == 0)
					{
						error = "error: register operand " + j + " (" + regs[j] + ") only allowed in fragment programs.";
						badreg = true;
						break;
					}
				}

				regs[j] = regs[j].substr(regs[j].indexOf(regFound.name) + regFound.name.length);
				reg = ~/\d+/;
				var idxmatched = false;

				if (isRelative)
					idxmatched = reg.match(relreg);

				else idxmatched = reg.match(regs[j]);
				var regidx = 0;

				if (idxmatched)
					regidx = Std.parseInt(reg.matched(0));

				if (regFound.range < regidx)
				{
					error = "error: register operand " + j + " (" + regs[j] + ") index exceeds limit of " + (regFound.range + 1) + ".";
					badreg = true;
					break;
				}

				var regmask = 0;
				var isDest = (j == 0 && (opFound.flags & OP_NO_DEST) == 0);
				var isSampler = (j == 2 && (opFound.flags & OP_SPECIAL_TEX) != 0);
				var reltype = 0;
				var relsel = 0;
				var reloffset = 0;

				if (isDest && isRelative)
				{
					error = "error: relative can not be destination";
					badreg = true;
					break;
				}

				reg = ~/(\.[xyzw]{1,4})/;

				if (reg.match(regs[j]))
				{
					var maskmatch = reg.matched(0);
					regmask = 0;
					var cv = 0;
					var maskLength = maskmatch.length;

					var k = 1;
					while (k < Std.int(maskLength))
					{
						cv = maskmatch.charCodeAt(k) - "x".charCodeAt(0);

						if (cv > 2)
							cv = 3;

						if (isDest)
							regmask |= 1 << cv;

						else regmask |= cv << ((k - 1) << 1);
						k++;
					}

					if (!isDest)
					{
						while (k <= 4)
						{
							regmask |= cv << ((k - 1) << 1); // repeat last
							k++;
						}
					}
				}
				else regmask = isDest ? 0xf : 0xe4; // id swizzle or mask

				if (isRelative)
				{
					reg = ~/[A-Za-z]{1,2}/ig;
					reg.match(relreg);
					var relname = reg.matched(0);
					var regFoundRel = REGMAP.get(relname);

					if (regFoundRel == null)
					{
						error = "error: bad index register";
						badreg = true;
						break;
					}

					reltype = regFoundRel.emitCode;
					reg = ~/(\.[xyzw]{1,1})/;

					if (!reg.match(relreg))
					{
						error = "error: bad index register select";
						badreg = true;
						break;
					}

					var selmatch = reg.matched(0);
					relsel = selmatch.charCodeAt(1) - "x".charCodeAt(0);

					if (relsel > 2)
						relsel = 3;

					reg = ~/\+\d{1,3}/ig;

					if (reg.match(relreg))
						reloffset = Std.parseInt(reg.matched(0));

					if (reloffset < 0 || reloffset > 255)
					{
						error = "error: index offset " + reloffset + " out of bounds. [0..255]";
						badreg = true;
						break;
					}

					if (verbose)
						Lib.trace("RELATIVE: type=" + reltype + "==" + relname + " sel=" + relsel + "==" + selmatch + " idx=" + regidx + " offset=" + reloffset);
				}

				if (verbose)
					Lib.trace("  emit argcode=" + regFound + "[" + regidx + "][" + regmask + "]");

				if (isDest)
				{
					agalcode.writeShort(regidx);
					agalcode.writeByte(regmask);
					agalcode.writeByte(regFound.emitCode);
					pad -= 32;
				}
				else
				{
					if (isSampler)
					{
						if (verbose)
							Lib.trace("  emit sampler");

						var samplerbits = 5; // type 5
						var optsLength = opts.length;
						var bias = 0.;

						var k = 0;
						while (k < Std.int(optsLength))
						{
							if (verbose)
								Lib.trace("    opt: " + opts[k]);

							var optfound:Sampler = SAMPLEMAP.get(opts[k]);

							if (optfound == null)
							{
								bias = Std.parseFloat(opts[k]);

								if (verbose)
									Lib.trace("    bias: " + bias);
							}
							else
							{
								if (optfound.flag != SAMPLER_SPECIAL_SHIFT)
									samplerbits &= ~(0xf << optfound.flag);

								samplerbits |= optfound.mask << optfound.flag;
							}

							k++;
						}

						agalcode.writeShort(regidx);
						agalcode.writeByte(Std.int(bias * 8.0));
						agalcode.writeByte(0);
						agalcode.writeUnsignedInt(samplerbits);

						if (verbose)
							Lib.trace("    bits: " + ( samplerbits - 5 ));

						pad -= 64;
					}
					else
					{
						if (j == 0)
						{
							agalcode.writeUnsignedInt(0);
							pad -= 32;
						}

						agalcode.writeShort(regidx);
						agalcode.writeByte(reloffset);
						agalcode.writeByte(regmask);
						agalcode.writeByte(regFound.emitCode);
						agalcode.writeByte(reltype);
						agalcode.writeShort(isRelative ? ( relsel | ( 1 << 15 ) ) : 0);

						pad -= 64;
					}
				}

				j++;
			}

			// pad unused regs
			j = 0;
			while (j < Std.int(pad))
			{
				agalcode.writeByte(0);
				j += 8;
			}

			if (badreg)
				break;

			i++;
		}

		if (error != "")
		{
			error += "\n  at line " + i + " " + lines[i];
			agalcode.length = 0;
			Lib.trace(error);
		}

		// trace the bytecode bytes if debugging is enabled
		if (debugEnabled)
		{
			var dbgLine = "generated bytecode:";
			var agalLength = Std.int(agalcode.length);
			var index = 0;

			while (index < agalLength)
			{
				if (( index % 16) == 0)
					dbgLine += "\n";

				if ((index % 4) == 0)
					dbgLine += " ";

				var byteStr = Std.string(agalcode[index]);// .toString( 16 );

				if (byteStr.length < 2)
					byteStr = "0" + byteStr;

				dbgLine += byteStr;
				index++;
			}

			Lib.trace(dbgLine);
		}

		if (verbose)
			Lib.trace("AGALMiniAssembler.assemble time: " + ((Lib.getTimer() - start) / 1000) + "s");

		return agalcode;
	}




}






 */

class OpCode {
    public final String name;
    public final int numRegister;
    public final int emitCode;
    public final int flags;

    public OpCode(String name, int numRegister, int emitCode, int flags) {
        this.name = name;
        this.numRegister = numRegister;
        this.emitCode = emitCode;
        this.flags = flags;
    }

    public String toString() {
        return "[OpCode name=\"" + name + "\", numRegister=" + numRegister + ", emitCode=" + emitCode + ", flags=" + flags + "]";
    }
}

class Register {
    public final String name;
    public final String longName;
    public final int emitCode;
    public final int range;
    public final int flags;

    public Register(String name, String longName, int emitCode, int range, int flags) {
        this.name = name;
        this.longName = longName;
        this.emitCode = emitCode;
        this.range = range;
        this.flags = flags;
    }

    public String toString() {
        return "[Register name=\"" + name + "\", longName=\"" + longName + "\", emitCode=" + emitCode + ", range=" + range + ", flags=" + flags + "]";
    }
}

class Sampler {
    public final String name;
    public final int flag;
    public final int mask;

    public Sampler(String name, int flag, int mask) {
        this.name = name;
        this.flag = flag;
        this.mask = mask;
    }

    public String toString() {
        return "[Sampler name=\"" + name + "\", flag=\"" + flag + "\", mask=" + mask + "]";
    }
}